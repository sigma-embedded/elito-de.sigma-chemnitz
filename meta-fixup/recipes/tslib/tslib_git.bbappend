FILESEXTRAPATHS_prepend := "${THISDIR}:"

SRC_URI += "\
  file://dev-name.patch \
  file://calibrate-eol.patch \
  file://plugin-link.patch \
"

CPPFLAGS += "-DUSE_INPUT_API"
