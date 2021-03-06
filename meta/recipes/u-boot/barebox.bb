_elito_skip := "${@elito_skip(d, 'barebox')}"

DESCRIPTION = "The barebox (formerly U-Boot v2) bootloader"
PRIORITY = "optional"
LICENSE = "GPLv2"
PROVIDES = "virtual/bootloader"
LIC_FILES_CHKSUM = "file://COPYING;md5=057bf9e50e1ca857d0eb97bfe4ba8e5d"

DEFAULT_PREFERENCE = "99"

DEPENDS += "lzop-native coreutils-native"
EXTRA_OEMAKE_prepend = "\
  -C ${S} \
  KBUILD_OUTPUT=${B} V=1 \
  CROSS_COMPILE="${TARGET_PREFIX}" \
  CC="${KERNEL_CC}" \
  LD="${KERNEL_LD}" \
  HOSTCC="${BUILD_CC}" \
  HOSTCPP="${BUILD_CPP}" \
"

unset CFLAGS
unset LDFLAGS

KBUILD_DEFCONFIG = "${UBOOT_MACHINE}"

PACKAGES         = "${PN}-dbg ${PN}-bin ${PN}-dev ${PN}"

FILES_${PN}      = "/boot/u-boot"
FILES_${PN}-dbg += "/boot/.debug"
FILES_${PN}-bin  = "/boot/u-boot.bin"

_repo = "barebox"

BAREBOX_SOC_FAMILY ?= "${@(d.getVar('SOC_FAMILY', True) or "").split(':')[0]}"

export ELITO_EXTKBUILD_DISABLED = "1"

B = "${WORKDIR}/build"

require u-boot-common.inc
inherit kernel-arch deploy cml1 elito-kconfig

do_configure() {
	oe_runmake olddefconfig
}

barebox_do_install() {
	install -D -p -m 0644 barebox.bin ${D}/boot/u-boot.bin
	install -D -p -m 0755 barebox     ${D}/boot/u-boot
}

do_install() {
	barebox_do_install
}

do_deploy () {
	set -e
	gitrev=`git -C '${S}' ls-remote . HEAD | sed '1s/^\(........\).*/\1/p;d'`
	uboot_image="u-boot-${MACHINE}-${PV}-${PR}-${gitrev}"

	install -D -p -m 0644 ${S}/barebox.bin ${DEPLOYDIR}/${uboot_image}.bin

	ln -s ${uboot_image} ${DEPLOYDIR}/${UBOOT_SYMLINK}
}

addtask deploy before do_build after do_compile
addtask builddeploy after do_compile before do_deploy

# keep this after do_XXX(); the soc rules can override previous tasks
include barebox_soc-${BAREBOX_SOC_FAMILY}.inc
