_elito_skip := "${@elito_skip(d, 'u-boot')}"

DESCRIPTION = "The U-Boot bootloader"
PRIORITY = "optional"
LICENSE = "GPL"
PROVIDES = "virtual/bootloader"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=a2c678cfd4a4d97135585cad908541c6"

DEFAULT_PREFERENCE = "99"

_repo = "u-boot"

include u-boot-common.inc
inherit deploy

B = "${S}"

PACKAGES         = "${PN}-dbg ${PN}-bin ${PN}"

FILES_${PN}      = "/boot/u-boot"
FILES_${PN}-dbg += "/boot/.debug"
FILES_${PN}-bin  = "/boot/u-boot.bin"

INSANE_SKIP_${PN} = "textrel ldflags"

_make() {
	${MAKE} -f '${ELITO_MAKEFILE_DIR}/Makefile.develcomp' \
		CFG=u-boot \
		CFG_NONDEVEL=1 \
		CFG_KERNEL_UART=${UBOOT_CONSOLE} \
		CFG_KERNEL_BAUD=${UBOOT_BAUD} \
		"$@"
}

do_configure[depends] += "elito-makefile:do_setup_makefile"
do_configure() {
	_make mrproper
	_make ${UBOOT_MACHINE}
}

do_compile() {
	_make
}

do_compile_append_mx6() {
	rm -f u-boot.ivt

	(
		dd if=/dev/zero bs=1024 count=1
		cat u-boot.imx
	) > u-boot.ivt
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
	install -p -m 0644 ${S}/u-boot.elf ${DEPLOYDIR}/${uboot_image}.elf
	ln -s ${uboot_image}.elf ${DEPLOYDIR}/${UBOOT_SYMLINK}.elf
}

do_deploy_machine_mx6() {
	install -D -p -m 0644 ${S}/u-boot.ivt ${DEPLOYDIR}/${uboot_image}.imx
	ln -s ${uboot_image}.imx ${DEPLOYDIR}/${UBOOT_SYMLINK}.imx
}

do_deploy[cleandirs] = "${DEPLOYDIR}"
do_deploy[vardeps] += "do_deploy_machine"
do_deploy () {
	uboot_image="u-boot-${MACHINE}-${PV}-${PR}"

	install -d ${DEPLOYDIR}
	install -D -p -m 0644 ${S}/u-boot.bin ${DEPLOYDIR}/${uboot_image}.bin
	install -D -p -m 0644 ${S}/u-boot     ${DEPLOYDIR}/${uboot_image}

	ln -s ${uboot_image}.bin ${DEPLOYDIR}/${UBOOT_SYMLINK}.bin
	ln -s ${uboot_image}     ${DEPLOYDIR}/${UBOOT_SYMLINK}

	do_deploy_machine
}
addtask deploy before do_build after do_compile
