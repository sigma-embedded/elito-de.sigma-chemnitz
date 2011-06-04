require ${OEMETA_TOPDIR}/meta-oe/recipes-graphics/xorg-driver/xorg-driver-video.inc
DESCRIPTION = "X.Org X server -- nouveau display driver"
DEPENDS += "libdrm"

SRCREV = "bc5dec2ca7ca7edc340a99bd73946e228117dfd8"
PE = "1"
_pv = "0.0"
PR = "${INC_PR}.0"

inherit gitpkgv

PV = "${_pv}+gitr${SRCPV}"
PKGV = "${_pv}+gitr${GITPKGV}"

SRC_URI = "git://anongit.freedesktop.org/git/nouveau/xf86-video-nouveau;protocol=git"
S = "${WORKDIR}/git"
