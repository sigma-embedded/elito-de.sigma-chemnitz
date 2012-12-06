PRINC := "${@int('${PRINC}') + 6}"

DEPENDS += "libv4l"
EXTRA_AUTORECONF += '--exclude=autopoint'

FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"
SRC_URI += "\
  file://v4l-raw.patch \
  file://other-bayer.patch \
"

oe_runconf_prepend() {
    sed -i -e "1a\\" -e 'GETTEXT_PACKAGE = @GETTEXT_PACKAGE@' po/Makefile.in.in
}
