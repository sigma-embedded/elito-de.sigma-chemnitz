DESCRIPTION = "Tools for working with the Windoze CE E-Boot bootloader"
LICENSE = "GPLv3"

_elito_skip := "${@elito_skip(d, 'ce-bootme')}"

SRCREV = "4bb05f14163e75becb9ea010aa98bcec70b9a8b4"
_pv = "0.1"
PR = "r3"

PV = "${_pv}+gitr${SRCPV}"
PKGV = "${_pv}+gitr${GITPKGV}"

SRC_URI = "${ELITO_GIT_REPO}/pub/ce-bootme.git;protocol=git"
S = "${WORKDIR}/git"

BBCLASSEXTEND = "native"
EXTRA_OEMAKE += "PROGRAM_PREFIX=ce-"

inherit gitpkgv

do_compile() {
    oe_runmake
}

do_install() {
    oe_runmake install DESTDIR=${D}
}
