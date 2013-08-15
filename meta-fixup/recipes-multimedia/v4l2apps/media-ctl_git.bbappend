FILESEXTRAPATHS_prepend := "${THISDIR}/media-ctl:"

SRCREV = "cfc025ce9f94a729e8fe9cc16466db288cd201da"
SRC_URI += "\
  file://pixelformats.patch \
  file://0001-give-out-frame-format-in-dot-graph.patch \
"
