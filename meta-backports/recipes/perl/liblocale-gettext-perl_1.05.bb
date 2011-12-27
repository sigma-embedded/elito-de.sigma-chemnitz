DESCRIPTION = "Locale::gettext - message handling functions"
SECTION = "libs"
LICENSE = "Artistic|GPLv1+"
DEPENDS = "virtual/libintl"
BBCLASSEXTEND = "native"
PR = "r1"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Artistic-2.0;md5=8bbc66f0ba93cec26ef526117e280266"

SRC_URI = "http://search.cpan.org/CPAN/authors/id/P/PV/PVANDRY/gettext-${PV}.tar.gz"

S = "${WORKDIR}/gettext-${PV}"

inherit cpan

SRC_URI[md5sum] = "f3d3f474a1458f37174c410dfef61a46"
SRC_URI[sha256sum] = "27367f3dc1be79c9ed178732756e37e4cfce45f9e2a27ebf26e1f40d80124694"

FILES_${PN}-dbg += "${libdir}/perl/vendor_perl/*/auto/Locale/gettext/.debug"
