DESCRIPTION	= "Socket CAN test utility"
LICENSE		= "GPLv3"

SRCREV		= "${AUTOREV}"
PV		= "0.1"
PR		= "r0"

SRC_URI		= "${ELITO_GIT_REPO}/can-test.git;protocol=https"
S		= "${WORKDIR}/git"

inherit elito-develcomp

do_compile() {
	oe_runmake
}

do_install() {
	oe_runmake install DESTDIR=${D}
}
