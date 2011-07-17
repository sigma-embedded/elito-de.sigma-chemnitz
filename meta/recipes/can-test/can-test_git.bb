DESCRIPTION	= "Socket CAN test utility"
LICENSE		= "GPLv3"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=2c12447f794c304d9cd353f87a432c9e"

_elito_skip := "${@elito_skip(d, 'can')}"

SRCREV		= "${AUTOREV}"
PV		= "0.2"
PR		= "r1"

SRC_URI		= "${ELITO_GIT_REPO}/pub/can-test.git;protocol=git"
S		= "${WORKDIR}/git"
RDEPENDS_${PN}	= "iproute2"

inherit elito-develcomp

PV_nondevel_append = "+gitr${SRCPV}"

do_compile() {
	oe_runmake
}

do_install() {
	oe_runmake install DESTDIR=${D}
}
