require upstart-setup.bb

_gitrepo           = "${ELITO_GIT_WS}/upstart-setup.git"

DEFAULT_PREFERENCE = "${@base_contains('ELITO_DEVEL_COMPONENTS','upstart-setup','','-1',d)}"
SRCREV   = "${AUTOREV}"
PV      .= "+gitr${SRCREV}"

SRC_URI		= "		\
	git://${_gitrepo};protocol=file	\
	file://paths						\
"

do_patch_late() {
    cd ${WORKDIR}
    mkdir -p common
    touch common/Makefile.common

    oe_runmake -C git dist R=${PV}
    tar xjf git/upstart-setup-${PV}.tar.bz2
}

addtask patch_late after do_unpack before do_patch do_install
