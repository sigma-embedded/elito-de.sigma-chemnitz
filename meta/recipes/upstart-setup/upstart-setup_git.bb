_pv		= "0.4.99"
PR		= "r3"

PV              = "${_pv}+gitr${SRCPV}"
PKGV            = "${_pv}+gitr${GITPKGV}"
SRCREV		= "b279dd64581a20ce40a93050931ace16dae52e43"

SRC_URI		= "${@elito_uri('${ELITO_GIT_REPO}/pub/upstart-setup.git',d)}"

require upstart-setup.inc
inherit gitpkgv

do_patch_late() {
    cd ${WORKDIR}
    mkdir -p common
    touch common/Makefile.common

    oe_runmake -C git dist R=${PV}
    tar xjf git/upstart-setup-${PV}.tar.bz2
}
addtask patch_late after do_unpack before do_patch do_install
