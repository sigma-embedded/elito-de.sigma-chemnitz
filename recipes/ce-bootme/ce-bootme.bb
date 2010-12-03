_elito_skip := "${@elito_skip(d, 'ce-bootme')}"

DESCRIPTION = "Tools for working with the Windoze CE E-Boot bootloader"
LICENSE = "GPLv3"

SRCREV = "${AUTOREV}"
_pv = "0.0"
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
