_elito_skip := "${@elito_skip(d, 'u-boot')}"

DESCRIPTION = "The U-Boot bootloader"
PRIORITY = "optional"
LICENSE = "GPL"
PROVIDES = "virtual/bootloader"

LIC_FILES_CHKSUM-legacy = "file://COPYING;md5=4c6cde5df68eff615d36789dc18edd3b"
LIC_FILES_CHKSUM-recent = "\
  file://Licenses/Exceptions;md5=338a7cb1e52d0d1951f83e15319a3fe7 \
  file://Licenses/bsd-2-clause.txt;md5=6a31f076f5773aabd8ff86191ad6fdd5 \
  file://Licenses/bsd-3-clause.txt;md5=4a1190eac56a9db675d58ebe86eaf50c \
  file://Licenses/eCos-2.0.txt;md5=b338cb12196b5175acd3aa63b0a0805c \
  file://Licenses/gpl-2.0.txt;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
  file://Licenses/ibm-pibs.txt;md5=c49502a55e35e0a8a1dc271d944d6dba \
  file://Licenses/lgpl-2.0.txt;md5=5f30f0716dfdd0d91eb439ebec522ec2 \
  file://Licenses/lgpl-2.1.txt;md5=4fbd65380cdd255951079008b364516c \
"

LIC_FILES_CHKSUM = "${LIC_FILES_CHKSUM-recent}"

DEFAULT_PREFERENCE = "99"

_repo = "u-boot"

include u-boot-common.inc
inherit deploy

PACKAGES         = "${PN}-dbg ${PN}-bin ${PN}"

FILES_${PN}      = "/boot/u-boot"
FILES_${PN}-dbg += "/boot/.debug"
FILES_${PN}-bin  = "/boot/u-boot.bin"

_make = "${MAKE} -f '${ELITO_MAKEFILE_DIR}/Makefile.develcomp' \
	CFG=u-boot _secwrap= \
	STRIP=: BUILD_STRIP=: \
	CFG_NONDEVEL=1 \
	CFG_KERNEL_UART=${UBOOT_CONSOLE} \
	CFG_KERNEL_BAUD=${UBOOT_BAUD}"

do_configure[depends] += "elito-makefile:do_setup_makefile"
do_configure() {
	cat << EOF >>config.mk
HOSTCFLAGS += \$(BUILD_CFLAGS)
HOSTCPPFLAGS += \$(BUILD_CPPFLAGS)
HOSTLDFLAGS += \$(BUILD_LDFLAGS)

unexport HOSTCFLAGS HOSTCPPFLAGS
EOF
}

do_compile() {
	${_make} ${UBOOT_MACHINE}
}

do_install() {
	install -D -p -m 0644 u-boot.bin ${D}/boot/u-boot.bin
	install -D -p -m 0755 u-boot     ${D}/boot/u-boot
}

do_deploy_machine() {
    :
}

## TODO: that's broken; there is no generic 'zynq-7000' override
do_deploy_machine_zynq-7000() {
	uboot_image="u-boot-${MACHINE}-${PV}-${PR}-${gitrev}.elf"

	install -p -m 0644 ${S}/u-boot ${DEPLOYDIR}/${uboot_image}
	ln -s ${uboot_image} ${DEPLOYDIR}/u-boot-${MACHINE}.elf
}

do_deploy[vardeps] += "do_deploy_machine"
do_deploy () {
	set -x
	gitrev=`git ls-remote . HEAD | sed '1s/^\(........\).*/\1/p;d'`
	uboot_image="u-boot-${MACHINE}-${PV}-${PR}-${gitrev}.bin"

	install -d ${DEPLOYDIR}
	install ${S}/u-boot.bin ${DEPLOYDIR}/${uboot_image}

	cd ${DEPLOYDIR}
	rm -f ${UBOOT_SYMLINK}
	ln -sf ${uboot_image} ${UBOOT_SYMLINK}

	do_deploy_machine
}
addtask deploy before do_build after do_compile
