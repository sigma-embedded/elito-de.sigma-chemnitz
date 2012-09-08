PRINC := "${@int('${PRINC}') + 1}"

FILESEXTRAPATHS_prepend := "${THISDIR}/media-ctl:"

SRC_URI += "\
  file://pixelformats.patch \
"

do_configure_prepend() {
    mv configure.in configure.ac
}
