SUMMARY = "imx28 rescue system loader"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"

_pv     = "0.1.0"
PR      = "r0"

SRCREV  = "f3f78fa778f1ef1924a646c623d754385807fbf0"
SRC_URI = "${ELITO_GIT_REPO}/pub/elito-imx28-load.git"

PV   = "${_pv}+gitr${SRCPV}"
PKGV = "${_pv}+gitr${GITPKGV}"

PACKAGE_ARCH = "${MACHINE_ARCH}"
COMPATIBLE_MACHINE = "mx28"

inherit gitpkgv

S = "${WORKDIR}/git"

do_compile() {
    oe_runmake
}

do_install() {
    install -D -p -m 0644 load-linux ${D}${libdir}/${MACHINE}/load-linux
}

FILES_${PN} += "${libdir}/${MACHINE}/load-linux"
FILES_${PN}-dbg += "${libdir}/${MACHINE}/.debug"
