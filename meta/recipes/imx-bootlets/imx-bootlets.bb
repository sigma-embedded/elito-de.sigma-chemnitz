SUMMARY = "iMX Bootlets"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"

_elito_skip := "${@elito_skip(d, 'imx-bootlets')}"

_pv = "10.12.01"

PV = "${_pv}+gitr${SRCPV}"
PKGV = "${_pv}+gitr${GITPKGV}"

IMX_BOOTLETS_BRANCH ?= "${KERNEL_BRANCH}"

SRCREV = "${AUTOREV}"
SRC_URI = "${ELITO_GIT_REPO}/imx-bootlets.git;branch=${IMX_BOOTLETS_BRANCH}"

inherit gitpkgv

S = "${WORKDIR}/git"

CFLAGS += "-fno-builtin-putc -fno-builtin-printf"
CFLAGS += "-mno-thumb-interwork -fomit-frame-pointer"
CFLAGS += "-fuse-linker-plugin -flto"
LDFLAGS += "-Wl,-gc-sections -fuse-linker-plugin -flto"

DEPENDS += "mx28-pins"

EXTRA_OEMAKE = " \
  BOARD=iMX28_EVK \
  CC='${CC}' \
  CFLAGS='${CFLAGS}' \
  LDFLAGS='${LDFLAGS}' \
  LD='${CC}' \
  AR='${AR}' \
  MACHINE='${MACHINE}' \
"

pkgdatadir = "${libdir}/${PN}"

do_configure() {
    sed -i \
	-e 's!./power_prep/!!' \
	-e 's!./boot_prep/!!' \
	*.bd
}

do_compile() {
    oe_runmake power_prep boot_prep
}

do_install() {
    install -D -p -m 0755 boot_prep/boot_prep \
      ${D}${pkgdatadir}/boot_prep

    install -D -p -m 0755 power_prep/power_prep \
      ${D}${pkgdatadir}/power_prep

    for i in uboot*.bd; do
	install -D -p -m 0644 $i ${D}${pkgdatadir}/$i
    done
}
