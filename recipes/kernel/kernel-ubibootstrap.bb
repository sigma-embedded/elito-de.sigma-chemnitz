DESCRIPTION	=  "UBI bootstrap kernel"
SECTION		=  "kernel"
LICENSE		=  "GPL"

PR		=  "r0"
PACKAGE_ARCH	=  "${MACHINE_ARCH}"

KERNEL_BOOTSTRAP_TFTP_IMAGE ?= ""
KERNEL_BOOTSTRAP_DEFCONFIG ?= "${@'${KERNEL_DEFCONFIG}'.replace('_defconfig','_bootstrap_defconfig')}"

SRC_URI += "file://ramfs.template \
	file://init \
	file://scan-dev \
"

_tftp_image      = "${KERNEL_BOOTSTRAP_TFTP_IMAGE}"
_defconfig       = "${KERNEL_BOOTSTRAP_DEFCONFIG}"
_kernel_output   = "arch/${ARCH}/boot/${KERNEL_IMAGETYPE}"

TARGET_CC_KERNEL_ARCH ?= ""
HOST_CC_KERNEL_ARCH ?= "${TARGET_CC_KERNEL_ARCH}"
TARGET_LD_KERNEL_ARCH ?= ""
HOST_LD_KERNEL_ARCH ?= "${TARGET_LD_KERNEL_ARCH}"

KERNEL_CC = "${CCACHE}${HOST_PREFIX}gcc${KERNEL_CCSUFFIX} ${HOST_CC_KERNEL_ARCH}"
KERNEL_LD = "${LD}${KERNEL_LDSUFFIX} ${HOST_LD_KERNEL_ARCH}"
export CROSS_COMPILE = "${TARGET_PREFIX}"

KERNEL_BOOTSTRAP_IMAGE_BASE_NAME ?= "${KERNEL_IMAGETYPE}-bootstrap-${PV}-${PR}-${MACHINE}"
KERNEL_BOOTSTRAP_IMAGE_SYMLINK_NAME ?= "${KERNEL_IMAGETYPE}-bootstrap-${MACHINE}"

KERNEL_BOOTSTRAP_LIBS = "ld-linux.so.* lib[cm].so.* libgcc_s.so.*"
#KERNEL_BOOTSTRAP_LIBS += "libdl.so.* libpthread.so.* libutil.so.*"
KERNEL_BOOTSTRAP_RAMFS_EXTRA ?= ":"

DEPENDS += " \
	busybox \
	mtd-utils \
	virtual/${HOST_PREFIX}gcc${KERNEL_CCSUFFIX} \
"

include elito-kernel.inc
inherit kernel-arch

do_prepramfs() {
	set -x

	sed -e 's!@S@!${STAGING_DIR}!g' \
            -e 's!@SH@!${STAGING_DIR}/${HOST_SYS}!g' \
            -e 's!@H@!${HOST_SYS}!g' \
            -e 's!@W@!${WORKDIR}!g' \
            -e 's!@R@!${IMAGE_ROOTFS}!g' \
		${WORKDIR}/ramfs.template > ramfs.txt

	for i in ash basename cat cp dd df dmesg echo false find free grep \
		gunzip gzip hostname ip kill less ln ls mkdir mknod mktemp \
		more \
		mount mv nc ping ps pwd readlink rm rmdir sed sh sha1sum \
		sleep sync tar touch true umount uname vi zcat; do
		echo slink /bin/$i busybox 777 0 0
	done >> ramfs.txt

	for i in arp fbdisk hwclock ifconfig insmod losetup lsmod modprobe nameif \
		pivot_root rmmod route setconsole switch_root sysctl syslogd; do \
		echo slink /sbin/$i ../bin/busybox 777 0 0
	done >> ramfs.txt

	(
		cd ${IMAGE_ROOTFS}/lib
		for i in ${KERNEL_BOOTSTRAP_LIBS}; do
			if test -L "$i"; then
				base=$(readlink "$i")
				echo slink /lib/$i "$base"  777 0 0
				echo file /lib/"$base" ${IMAGE_ROOTFS}/lib/$base 755 0 0
			else
				echo file /lib/"$i" ${IMAGE_ROOTFS}/lib/$i 755 0 0
			fi
		done
	) >> ramfs.txt

	${KERNEL_BOOTSTRAP_RAMFS_EXTRA}

	rm -f usr/initramfs_*cpio*
}

do_configure() {
	echo 'CONFIG_INITRAMFS_SOURCE="ramfs.txt"' >> .config
	oe_runmake -f mf oldconfig

	! grep -q -i -e '^CONFIG_MODULES=y$' .config || {
		oefatal "CONFIG_MODULE not possible for bootstrap kernels"
	}

}

do_compile() {
	oe_runmake -f mf
	oe_runmake -f mf ${KERNEL_IMAGETYPE}
}

do_deploy() {
	set -x

	install -d ${DEPLOY_DIR_IMAGE}
	install -p -m 0644 ${_kernel_output} ${DEPLOY_DIR_IMAGE}/${KERNEL_BOOTSTRAP_IMAGE_BASE_NAME}.img

	cd ${DEPLOY_DIR_IMAGE}
	rm -f ${KERNEL_BOOTSTRAP_IMAGE_SYMLINK_NAME}.img
	ln -sf ${KERNEL_BOOTSTRAP_IMAGE_BASE_NAME}.img ${KERNEL_BOOTSTRAP_IMAGE_SYMLINK_NAME}.img
}

do_prepramfs[dirs] = "${S}"
do_deploy[dirs] = "${S}"
addtask prepramfs before do_configure after do_patch
addtask deploy before do_package after do_install
