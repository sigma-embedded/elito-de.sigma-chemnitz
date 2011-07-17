DESCRIPTION = "Compress::Zlib - Interface to zlib compression library"
SECTION = "libs"
LICENSE = "Artistic|GPLv1+"
PR = "r0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Artistic;md5=f921793d03cc6d63ec4b15e9be8fd3f8"

RDEPENDS_${PN} += "libio-compress-base-perl libcompress-raw-zlib-perl libio-compress-zlib-perl"
RDEPENDS_${PN}_virtclass-native = ""

SRC_URI = "http://search.cpan.org/CPAN/authors/id/P/PM/PMQS/Compress-Zlib-${PV}.tar.gz"

S = "${WORKDIR}/Compress-Zlib-${PV}"

inherit cpan

FILES_${PN} = "${PERLLIBDIRS}/auto/Compress/Zlib/* \
               ${PERLLIBDIRS}/Compress \
               ${datadir}/perl5"

BBCLASSEXTEND="native"

SRC_URI[md5sum] = "689ba2cc399b019d0bf76a0575c32947"
SRC_URI[sha256sum] = "9b4c6fde1c972016fcbea1f019d143261ac0f5410652ea91571d7eedd22831cc"
