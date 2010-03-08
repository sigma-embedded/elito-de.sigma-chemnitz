_elito_skip := "${@elito_skip(d, 'u-boot')}"

DESCRIPTION      = "The U-Boot bootloader"
SECTION          = "bootloaders"
PRIORITY         = "optional"
LICENSE          = "GPL"
PROVIDES         = "virtual/bootloader"
PACKAGE_ARCH     = "${MACHINE_ARCH}"
PV               = "${UBOOT_VERSION}"
PR               = "r1"

DEFAULT_PREFERENCE = "99"
SRCREV           = "${AUTOREV}"

UBOOT_BRANCH	?= "${KERNEL_BRANCH}"
UBOOT_MACHINE	?= "${MACHINE}_config"
UBOOT_REV       ?= "branch=${UBOOT_VERSION}/${UBOOT_BRANCH}"
UBOOT_SYMLINK	?= "u-boot-${MACHINE}.bin"

SRCURI_SPEC_append	=  ";${UBOOT_REV}"
COMPONENT		=  u-boot

inherit elito-develcomp


S                = "${WORKDIR}/git"
PACKAGES         = "${PN}-dbg ${PN}-bin ${PN}"

FILES_${PN}      = "/boot/u-boot"
FILES_${PN}-dbg  = "/boot/.debug"
FILES_${PN}-bin  = "/boot/u-boot.bin"

TFTP_SERVER	?= ''
KERNEL_TFTP_IMAGE ?= ''

EXTRA_OEMAKE     = "\
	CROSS_COMPILE='${TARGET_PREFIX}' \
	AR='${AR}' AS='{AS}' CC='${CC}' CPP='${CPP}' \
	LD='${LD}' NM='${NM}' STRIP='${STRIP}' \
	OBJCOPY='${OBJCOPY}' OBJDUMP='${OBJDUMP}' \
	RANLIB='${RANLIB}' HOSTCC='${BUILD_CC}' \
	HOSTCFLAGS='${BUILD_CFLAGS}'	\
	${@base_conditional('KERNEL_TFTP_IMAGE','','','CFG_BOOTFILE=$(basename ${KERNEL_TFTP_IMAGE})',d)}	\
	${@base_conditional('TFTP_SERVER','','','CFG_SERVERIP=${TFTP_SERVER}',d)} \
	CFG_NFSROOT='MK_STR(CONFIG_SERVERIP) ":${IMAGE_ROOTFS}"' \
"

do_configure() {
        unset LDFLAGS CPPFLAGS CFLAGS
	oe_runmake ${UBOOT_MACHINE}
	oe_runmake include/autoconf.mk
	oe_runmake clean
}

do_compile() {
	unset LDFLAGS CPPFLAGS CFLAGS
	set -x
	oe_runmake
}

do_install() {
	install -D -p -m 0644 u-boot.bin ${D}/boot/u-boot.bin
	install -D -p -m 0755 u-boot     ${D}/boot/u-boot
}

do_deploy () {
	set -x
	gitrev=`git ls-remote . HEAD | sed '1s/^\(........\).*/\1/p;d'`
	uboot_image="u-boot-${MACHINE}-${PV}-${PR}-${gitrev}.bin"

	install -d ${DEPLOY_DIR_IMAGE}
	install ${S}/u-boot.bin ${DEPLOY_DIR_IMAGE}/${uboot_image}
	package_stagefile_shell ${DEPLOY_DIR_IMAGE}/${uboot_image}

	cd ${DEPLOY_DIR_IMAGE}
	rm -f ${UBOOT_SYMLINK}
	ln -sf ${uboot_image} ${UBOOT_SYMLINK}
	package_stagefile_shell ${DEPLOY_DIR_IMAGE}/${UBOOT_SYMLINK}
}
do_deploy[dirs] = "${S}"
addtask deploy before do_build after do_compile
