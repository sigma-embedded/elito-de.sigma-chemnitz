SUMMARY = "imx28 rescue system loader"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"

_pv     = "0.1.0"
PR      = "r0"

SRCREV = "${AUTOREV}"
#SRCREV  = "6aeeb7c37994ae3d5686585a145088a17f6ef9db"
#SRC_URI = "${ELITO_GIT_REPO}/pub/elito-imx28-load.git"
SRC_URI = "git:///home/ensc/src/elito-imx28-load;protocol=file"

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
