TSLIBCONF_PATH = "${@base_contains(\
  'DISTRO_FEATURES','res_touch','','${THISDIR}/files-captouch:',d)}"
FILESEXTRAPATHS_prepend := "${TSLIBCONF_PATH}${THISDIR}/files:"

SRC_URI += "\
  file://dev-name.patch \
  file://calibrate-eol.patch \
  file://plugin-link.patch \
"

CPPFLAGS += "-DUSE_INPUT_API"
