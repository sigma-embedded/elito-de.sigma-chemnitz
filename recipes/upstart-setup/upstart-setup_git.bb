require upstart-setup.bb

COMPONENT  = "upstart-setup"
inherit elito-develcomp

do_patch_late() {
    cd ${WORKDIR}
    mkdir -p common
    touch common/Makefile.common

    oe_runmake -C git dist R=${PV}
    tar xjf git/upstart-setup-${PV}.tar.bz2
}
