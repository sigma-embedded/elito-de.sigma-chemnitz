TSLIBCONF_PATH = "${@bb.utils.contains(\
  'DISTRO_FEATURES','restouch','','${THISDIR}/files-captouch:',d)}"
FILESEXTRAPATHS_prepend := "${TSLIBCONF_PATH}${THISDIR}/files:"

SRC_URI += "\
  file://dev-name.patch \
  file://calibrate-eol.patch \
  file://plugin-link.patch \
"

CPPFLAGS += "-DUSE_INPUT_API"

do_install_append() {
    test -s ${D}${sysconfdir}/ts.conf || rm -f ${D}${sysconfdir}/ts.conf
    ${@bb.utils.contains("PROJECT_FEATURES", "select-touch", "rm -f ${D}${sysconfdir}/ts.conf", "", d)}
}
