OVERRIDES .= ":${@base_contains('ELITO_DEVEL_COMPONENTS',\
		 bb.data.getVar('COMPONENT', d, 1),'devel','nondevel',d)}"

DEFAULT_PREFERENCE_nondevel = -1
DEFAULT_PREFERENCE_devel    = ""

SRCREV_nondevel          = "0"
SRCREV_devel             = "${AUTOREV}"

SRC_URI_prepend_devel	 = " git://${ELITO_GIT_WS}/${COMPONENT}.git;protocol=file "

_pvextra_nondevel	 = ""
_pvextra_devel		 = "+gitr${SRCREV}"
PV_append		 = "${_pvextra}"

do_patch_late() {
	:
}
addtask patch_late after do_unpack before do_patch do_install
