require ${OEDEV_TOPDIR}/recipes/xorg-driver/xorg-driver-video.inc
DESCRIPTION = "X.Org X server -- nouveau display driver"
DEPENDS += "libdrm"

SRCREV = "aa2821a42706ac7b69703d1869e2d00a4ced9f4b"
PE = "1"
_pv = "0.0"
PR = "${INC_PR}.0"

inherit gitpkgv

PV = "${_pv}+gitr${SRCPV}"
PKGV = "${_pv}+gitr${GITPKGV}"

SRC_URI = "git://anongit.freedesktop.org/git/nouveau/xf86-video-nouveau;protocol=git"
S = "${WORKDIR}/git"
