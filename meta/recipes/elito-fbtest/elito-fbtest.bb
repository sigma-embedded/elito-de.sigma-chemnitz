_elito_skip := "${@elito_skip(d, 'fb')}"

DESCRIPTION	=  "Framebuffer testutility"
LICENSE		=  "GPLv3"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"

_pv = "0.1"
PR = "r0"

PV = "${_pv}+gitr${SRCPV}"
PKGV = "${_pv}+gitr${GITPKGV}"

inherit gitpkgv

SRC_URI		=  "${ELITO_GIT_REPO}/pub/fbtest.git;protocol=git"
SRCREV		=  "fb132343a92d919594b2281f0b59304ea5ae737a"
S		=  "${WORKDIR}/git"

do_compile() {
	oe_runmake
}

do_install() {
	set -x
	install -d ${D}${bindir}
	install -p -m 0755 fbtest ${D}${bindir}/elito-fbtest
}
