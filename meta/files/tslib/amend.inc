SRC_URI += "\
  file://dev-name.patch \
  file://open-mode.patch \
  file://calibrate-eol.patch \
  file://plugin-link.patch \
"

CPPFLAGS += "-DUSE_INPUT_API"
