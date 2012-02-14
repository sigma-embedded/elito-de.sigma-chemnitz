LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

PV = "1.0"

SRC_URI = "http://www.kreuzholzen.de/src/arm-memspeed/arm-memspeed-${PV}.tar.bz2"

SRC_URI[md5sum] = "daf1824f1d1f0a6dd8021cc825b2a8b3"
SRC_URI[sha256sum] = "62b5e99b23b4ef125209002293efafede546d18137ab4a2a98984e43df40ed0e"

inherit autotools
