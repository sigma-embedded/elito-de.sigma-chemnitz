require upstart-setup.bb

_gitrepo           = "${ELITO_GIT_WS}/upstart-setup.git"

OVERRIDES         .= ":${@base_contains('ELITO_DEVEL_COMPONENTS','upstart-setup','devel','nondevel',d)}" 

DEFAULT_PREFERENCE       = -1
DEFAULT_PREFERENCE_devel = ""
SRCREV                   = "0"
SRCREV_devel             = "${AUTOREV}"

PV      .= "+gitr${SRCREV}"

SRC_URI			= ""
SRC_URI_devel		= "		\
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
