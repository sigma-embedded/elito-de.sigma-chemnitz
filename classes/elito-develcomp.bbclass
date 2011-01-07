## -*- python -*-
COMPONENT ?= "${PN}"
OVERRIDES .= ":${@base_contains('ELITO_DEVEL_COMPONENTS',\
                 bb.data.getVar('COMPONENT', d, 1),'devel','nondevel',d)}"

DEFAULT_PREFERENCE_nondevel = -1
DEFAULT_PREFERENCE_devel    = ""

DEPENDS_append   = " elito-develcomp-native"
SRCURI_SPEC     ?= "protocol=file"

python () {
    import bb, os.path

    c    = bb.data.getVar('COMPONENT', d, 1)
    if not base_contains('ELITO_DEVEL_COMPONENTS', c, True, False, d):
        bb.debug("Using normal version for component '%s'" % c)
        return

    bb.debug("Using development snapshot for component '%s'" % c)

    base = bb.data.getVar('ELITO_GIT_WS', d, 1)
    p    = bb.data.getVar('PROJECT_NAME', d, 1)
    path = None

    for suffix in (',' + p + '.git', ',' + p, '.git', ''):
        path = os.path.join(base, c + suffix)
        bb.debug("Checking %s" % path)
        if os.path.isdir(path):
            break
        path = None

    if not path:
        raise Exception("No development repository found")

    uri = bb.data.getVar('SRC_URI', d, 0).split()
    bb.debug("Original SRC_URI: %s" % uri)
    uri[0] = "git://%s;${SRCURI_SPEC}" % path
    bb.debug("Modified SRC_URI: %s" % uri)


    bb.data.setVar('SRC_URI', ' '.join(uri), d)

    rev = bb.data.getVar('AUTOREV', d, 1)
    bb.debug("Rev: %s" % rev)
    bb.data.setVar('SRCREV', rev , d)
    bb.data.setVar('PV', '%s+gitr%s' % (bb.data.getVar('PV', d, 0),
                                        rev), d)
}

do_patch_late() {
	:
}
addtask patch_late after do_unpack before do_patch do_install
