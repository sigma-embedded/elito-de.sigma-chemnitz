FILESEXTRAPATHS_prepend := "${THISDIR}/media-ctl:"

SRCREV = "114c1b274edc40e07e9b99a435d26438f5b99943"
SRC_URI += "\
  file://pixelformats.patch \
  file://0001-give-out-frame-format-in-dot-graph.patch \
  file://xtra-formats.patch \
"
