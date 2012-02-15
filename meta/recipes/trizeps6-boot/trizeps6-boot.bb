SUMMARY = "Keith & Koep Trizeps6 boot helper"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"

COMPATIBLE_MACHINE = "kk-trizeps6"

SRCREV = "1090e43845a5a1a6f4ce68780b21018eec469722"
_pv = "0.0"
PV = "${_pv}+gitr${SRCPV}"
PKGV = "${_pv}+gitr${GITPKGV}"

SRC_URI = "${@elito_uri('${ELITO_GIT_REPO}/pub/trizeps6-boot.git', d)};protocol=git"

inherit gitpkgv

S = "${WORKDIR}/git"

BBCLASSEXTEND = "cross"

xdeps = ""
xdeps_virtclass-cross = "trizeps6-boot"

DEPENDS += "${xdeps}"

EXTRA_OEMAKE_virtclass-cross = " \
  BOOTCODE=${STAGING_DIR_TARGET}/usr/include/trizeps6-boot/bootcode.h \
"

do_compile() {
    oe_runmake trizeps6-start
}

do_installbase() {
    install -D -p -m 0755 trizeps6-start ${D}${bindir}/trizeps6-start
}

do_install() {
    do_installbase
    install -D -p -m 0644 bootcode.h ${D}${includedir}/trizeps6-boot/bootcode.h
}

do_install_virtclass-cross() {
    do_installbase
}
