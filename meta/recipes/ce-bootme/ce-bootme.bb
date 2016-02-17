DESCRIPTION = "Tools for working with the Windoze CE E-Boot bootloader"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"

_elito_skip := "${@elito_skip(d, 'ce-bootme')}"

SRCREV = "3b206ae5d527b93b8ef9775d35c9f71364f95a0d"
_pv = "0.6"

PV = "${_pv}+gitr${SRCPV}"
PKGV = "${_pv}+gitr${GITPKGV}"

SRC_URI = "${ELITO_PUBLIC_GIT_REPO}/ce-bootme.git;protocol=git"
S = "${WORKDIR}/git"

BBCLASSEXTEND = "native"
EXTRA_OEMAKE += "PROGRAM_PREFIX=ce-"

inherit gitpkgv

do_compile() {
    oe_runmake -e
}

do_install() {
    oe_runmake -e install DESTDIR=${D}
}
