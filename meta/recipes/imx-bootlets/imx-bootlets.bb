SUMMARY = "iMX Bootlets"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"

_elito_skip := "${@elito_skip(d, 'imx-bootlets')}"

_pv = "10.12.01"
PR = "r0"

PV = "${_pv}+gitr${SRCPV}"
PKGV = "${_pv}+gitr${GITPKGV}"

SRCREV = "${AUTOREV}"
SRC_URI = "${@elito_uri('${ELITO_GIT_REPO}/imx-bootlets.git',d)};branch=tq-mx28"

inherit gitpkgv

S = "${WORKDIR}/git"

CFLAGS += "-fno-builtin-putc -fno-builtin-printf -ffunction-sections"
CFLAGS += "-mno-thumb-interwork -fomit-frame-pointer"
LDFLAGS += "-gc-sections"

#CFLAGS += "-fno-eliminate-unused-debug-types -fno-function-sections"
#CFLAGS += "-fuse-linker-plugin -flto -g2"

EXTRA_OEMAKE = " \
  BOARD=iMX28_EVK \
  CC='${CC}' \
  CFLAGS='${CFLAGS}' \
  LDFLAGS='${LDFLAGS}' \
  LD='${CC}' \
  AR='${AR}' \
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
