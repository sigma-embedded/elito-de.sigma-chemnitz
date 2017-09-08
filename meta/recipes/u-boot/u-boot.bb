_elito_skip := "${@elito_skip(d, 'u-boot')}"

DESCRIPTION = "The U-Boot bootloader"
PRIORITY = "optional"
LICENSE = "GPL"
PROVIDES = "virtual/bootloader"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=a2c678cfd4a4d97135585cad908541c6"

DEFAULT_PREFERENCE = "99"
UBOOT_DEFCONFIG ??= "${UBOOT_MACHINE}"
KBUILD_DEFCONFIG_pn-${BPN} ?= "${UBOOT_DEFCONFIG}"

ARCH_DEFCONFIG ?= '${S}/configs/${KBUILD_DEFCONFIG}'

_repo = "u-boot"

include u-boot-common.inc
inherit deploy cml1 elito-kconfig

B = "${WORKDIR}/build"

PACKAGES         = "${PN}-dbg ${PN}-bin ${PN}"

FILES_${PN}      = "/boot/u-boot"
FILES_${PN}-dbg += "/boot/.debug"
FILES_${PN}-bin  = "/boot/u-boot.bin"

INSANE_SKIP_${PN} = "textrel ldflags"

EXTRA_OEMAKE = "\
  -C ${S} O=${B} V=1 \
  CROSS_COMPILE='${TARGET_PREFIX}' \
  CC='${TARGET_PREFIX}gcc ${TOOLCHAIN_OPTIONS}' \
  HOSTCC='${BUILD_CC} ${BUILD_CFLAGS} ${BUILD_LDFLAGS}' \
"

do_prepare_config_prepend() {
	touch ${LOCAL_FRAGMENT}
}

do_configure() {
	! test -e ${B}/.config || oe_runmake olddefconfig
}

do_compile() {
	unset LDFLAGS CFLAGS CPPFLAGS LD

	if ! test -e .config; then
		oe_runmake ${UBOOT_MACHINE}
	fi

	oe_runmake ${UBOOT_MAKE_TARGET}
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
	install -p -m 0644 ${B}/u-boot.elf ${DEPLOYDIR}/${uboot_image}.elf
	ln -s ${uboot_image}.elf ${DEPLOYDIR}/${UBOOT_SYMLINK}.elf
}

do_deploy_machine_mx6() {
	install -D -p -m 0644 ${B}/u-boot.ivt ${DEPLOYDIR}/${uboot_image}.imx
	ln -s ${uboot_image}.imx ${DEPLOYDIR}/${UBOOT_SYMLINK}.imx
}

UBOOT_MLO_SYMLINK ??= "MLO-${MACHINE}"

do_deploy_machine_ti33xx() {
	mlo_image="MLO-${MACHINE}-${PV}-${PR}"

	install -D -p -m 0644 ${B}/MLO ${DEPLOYDIR}/${mlo_image}.img
	ln -s ${mlo_image}.img ${DEPLOYDIR}/${UBOOT_MLO_SYMLINK}.img
	ln -s ${mlo_image}.img ${DEPLOYDIR}/MLO

	install -D -p -m 0644 ${B}/spl/u-boot-spl ${DEPLOYDIR}/${mlo_image}.elf
	ln -s ${mlo_image}.elf ${DEPLOYDIR}/${UBOOT_MLO_SYMLINK}.elf
}

do_deploy[cleandirs] = "${DEPLOYDIR}"
do_deploy[vardeps] += "do_deploy_machine"
do_deploy () {
	uboot_image="u-boot-${MACHINE}-${PV}-${PR}"

	install -d ${DEPLOYDIR}
	install -D -p -m 0644 ${B}/u-boot.bin ${DEPLOYDIR}/${uboot_image}.bin
	install -D -p -m 0644 ${B}/u-boot.img ${DEPLOYDIR}/${uboot_image}.img
	install -D -p -m 0644 ${B}/u-boot     ${DEPLOYDIR}/${uboot_image}

	ln -s ${uboot_image}.bin ${DEPLOYDIR}/${UBOOT_SYMLINK}.bin
	ln -s ${uboot_image}.img ${DEPLOYDIR}/${UBOOT_SYMLINK}.img
	ln -s ${uboot_image}     ${DEPLOYDIR}/${UBOOT_SYMLINK}

	do_deploy_machine
}
addtask deploy before do_build after do_compile
