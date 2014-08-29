DEPENDS += "libv4l"
EXCLUDE_AUTOPOINT = "true"

FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"
SRC_URI += "\
  file://v4l-raw.patch \
  file://other-bayer.patch \
  file://05d4f8183400f6157346ea06950def23394ed1aa.patch \
"

oe_runconf_prepend() {
    sed -i -e "1a\\" -e 'GETTEXT_PACKAGE = @GETTEXT_PACKAGE@' ${S}/po/Makefile.in.in
}
