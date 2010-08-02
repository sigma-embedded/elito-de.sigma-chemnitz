_elito_skip := "${@elito_skip(d, 'u-boot')}"

DESCRIPTION      = "The U-Boot bootloader"
SECTION          = "bootloaders"
PRIORITY         = "optional"
LICENSE          = "GPL"
PROVIDES         = "virtual/bootloader"
PACKAGE_ARCH     = "${MACHINE_ARCH}"
PV               = "${UBOOT_VERSION}"
PR               = "r6"

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

UBOOT_CONSOLE	?= "${@'${SERIAL_CONSOLE}'.split()[1]}"
UBOOT_BAUD	?= "${@'${SERIAL_CONSOLE}'.split()[0]}"

_make = "${MAKE} -e -f '${TMPDIR}/Makefile.develcomp' \
	CFG=u-boot _secwrap= \
	CFG_NONDEVEL=1 \
	CFG_KERNEL_UART=${UBOOT_CONSOLE} \
	CFG_KERNEL_BAUD=${UBOOT_BAUD}"

do_configure() {
	${_make} ${UBOOT_MACHINE}
	${_make} include/autoconf.mk || :
	${_make} clean
}

do_compile() {
	${_make}
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
