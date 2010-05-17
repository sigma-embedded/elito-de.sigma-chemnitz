_elito_skip := "${@elito_skip(d, 'can')}"

DESCRIPTION	= "Socket CAN test utility"
LICENSE		= "GPLv3"

SRCREV		= "${AUTOREV}"
PV		= "0.2"
PR		= "r0"

SRC_URI		= "${ELITO_GIT_REPO}/pub/can-test.git;protocol=git"
S		= "${WORKDIR}/git"

inherit elito-develcomp

PV_nondevel_append = "+gitr${SRCPV}"

do_compile() {
	oe_runmake
}

do_install() {
	oe_runmake install DESTDIR=${D}
}
