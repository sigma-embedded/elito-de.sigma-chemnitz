_elito_skip := "${@elito_skip(d, 'fb')}"

DESCRIPTION	=  "Framebuffer testutility"
LICENSE		=  "GPLv3"

PV = "0.1+gitr${SRCPV}"
PR = "r0"

SRC_URI		=  "${ELITO_GIT_REPO}/fbtest.git;protocol=https"
SRCREV		=  "${AUTOREV}"
S		=  "${WORKDIR}/git"

do_compile() {
	oe_runmake
}

do_install() {
	set -x
	install -d ${D}${bindir}
	install -p -m 0755 fbtest ${D}${bindir}/elito-fbtest
}
