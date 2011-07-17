_elito_skip := "${@elito_skip(d, 'fb')}"

DESCRIPTION	=  "Framebuffer testutility"
LICENSE		=  "GPLv3"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=2c12447f794c304d9cd353f87a432c9e"

PV = "0.1+gitr${SRCPV}"
PR = "r0"

SRC_URI		=  "${ELITO_GIT_REPO}/pub/fbtest.git;protocol=git"
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
