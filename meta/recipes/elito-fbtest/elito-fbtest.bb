_elito_skip := "${@elito_skip(d, 'fb')}"

DESCRIPTION	=  "Framebuffer testutility"
LICENSE		=  "GPLv3"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"

_pv = "0.3"

PV = "${_pv}+gitr${SRCPV}"
PKGV = "${_pv}+gitr${GITPKGV}"

inherit gitpkgv

SRC_URI		=  "git://github.com/sigma-embedded/fbtest.git;protocol=https"
SRCREV		=  "51dbb47a886f66b4bc4b52dfe186975dd054a1dc"
S		=  "${WORKDIR}/git"

do_compile() {
	oe_runmake
}

do_install() {
	set -x
	install -d ${D}${bindir}
	install -p -m 0755 fbtest ${D}${bindir}/elito-fbtest
}
