DESCRIPTION	= "Socket CAN test utility"
LICENSE		= "GPLv3"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"

_elito_skip := "${@elito_skip(d, 'can', None, 'MACHINE_FEATURES')}"

SRCREV		= "fdc8cdf4b7447a32cd67b28612fedc0fc232b095"
_pv		= "0.2"
PV              = "${_pv}+gitr${SRCPV}"
PKGV            = "${_pv}+gitr${GITPKGV}"

SRC_URI		= "${@elito_uri('${ELITO_PUBLIC_GIT_REPO}/can-test.git',d)}"
S		= "${WORKDIR}/git"
RDEPENDS_${PN}	= "iproute2"

inherit gitpkgv

do_compile() {
	oe_runmake
}

do_install() {
	oe_runmake install DESTDIR=${D}
}
