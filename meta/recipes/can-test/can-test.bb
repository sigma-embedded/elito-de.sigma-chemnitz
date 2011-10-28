DESCRIPTION	= "Socket CAN test utility"
LICENSE		= "GPLv3"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=2c12447f794c304d9cd353f87a432c9e"

_elito_skip := "${@elito_skip(d, 'can')}"

SRCREV		= "fdc8cdf4b7447a32cd67b28612fedc0fc232b095"
_pv		= "0.2"
PV              = "${_pv}+gitr${SRCPV}"
PKGV            = "${_pv}+gitr${GITPKGV}"
PR		= "r1"

SRC_URI		= "${@elito_uri('${ELITO_GIT_REPO}/pub/can-test.git',d)}"
S		= "${WORKDIR}/git"
RDEPENDS_${PN}	= "iproute2"

inherit gitpkgv

do_compile() {
	oe_runmake
}

do_install() {
	oe_runmake install DESTDIR=${D}
}
