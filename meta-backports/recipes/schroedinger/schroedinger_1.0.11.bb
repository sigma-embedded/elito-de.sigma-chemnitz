require schroedinger.inc

SRC_URI += "file://stdint.patch"

inherit autotools

PACKAGES =+ "gst-plugin-schroedinger-dbg gst-plugin-schroedinger-dev gst-plugin-schroedinger"
FILES_gst-plugin-schroedinger = "${libdir}/gstreamer-0.10/*.so"
FILES_gst-plugin-schroedinger-dbg = "${libdir}/gstreamer-0.10/.debug"
FILES_gst-plugin-schroedinger-dev = "${libdir}/gstreamer-0.10/*.*a"

SRC_URI[schroedingertargz.md5sum] = "da6af08e564ca1157348fb8d92efc891"
SRC_URI[schroedingertargz.sha256sum] = "1e572a0735b92aca5746c4528f9bebd35aa0ccf8619b22fa2756137a8cc9f912"
