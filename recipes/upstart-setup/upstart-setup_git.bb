PV		= "0.4.99+git${SRCPV}"
PR		= "r3"
SRCREV		= "${AUTOREV}"

SRC_URI		= "${ELITO_GIT_REPO}/pub/upstart-setup.git;protocol=git"

require upstart-setup.inc

do_patch_late() {
    cd ${WORKDIR}
    mkdir -p common
    touch common/Makefile.common

    oe_runmake -C git dist R=${PV}
    tar xjf git/upstart-setup-${PV}.tar.bz2
}
addtask patch_late after do_unpack before do_patch do_install
