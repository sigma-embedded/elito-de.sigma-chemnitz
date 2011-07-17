require schroedinger.inc

SRC_URI += "file://stdint.patch"
PR = "${INC_PR}.0"

inherit autotools

PACKAGES =+ "gst-plugin-schroedinger-dbg gst-plugin-schroedinger-dev gst-plugin-schroedinger"
FILES_gst-plugin-schroedinger = "${libdir}/gstreamer-0.10/*.so"
FILES_gst-plugin-schroedinger-dbg = "${libdir}/gstreamer-0.10/.debug"
FILES_gst-plugin-schroedinger-dev = "${libdir}/gstreamer-0.10/*.*a"

SRC_URI[schroedingertargz.md5sum] = "9de088ccc314bb9e766cb3aa6510a0ef"
SRC_URI[schroedingertargz.sha256sum] = "9a45c4f8d6197a641a9b06ab9b59ec02ad9986723fd855528a00ec3477a71964"
