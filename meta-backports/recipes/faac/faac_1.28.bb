DESCRIPTION = "Library for reading some sort of media format."
SECTION = "libs"
PRIORITY = "optional"
DEPENDS = ""
LICENSE = "LGPL"
LIC_FILES_CHKSUM = "file://COPYING;md5=3bf50002aefd002f49e7bb854063f7e7"

PR = "r0"

inherit autotools

SRC_URI = "${SOURCEFORGE_MIRROR}/faac/${PN}-${PV}.tar.gz;name=tarball \
	file://gcc44.patch"
S = "${WORKDIR}/${P}"

SRC_URI[tarball.md5sum] = "80763728d392c7d789cde25614c878f6"
SRC_URI[tarball.sha256sum] = "c5141199f4cfb17d749c36ba8cfe4b25f838da67c22f0fec40228b6b9c3d19df"

PACKAGES =+ "lib${PN} lib${PN}-dev"

FILES_${PN} = " ${bindir}/faac "
FILES_lib${PN} = " ${libdir}/libfaac.so.*"
FILES_lib${PN}-dev = " ${includedir}/faac.h ${includedir}/faaccfg.h ${libdir}/libfaac.so ${libdir}/libfaac.la ${libdir}/libfaac.a "


SRC_URI[md5sum] = "e72dc74db17b42b06155613489077ad7"
SRC_URI[sha256sum] = "a5844ff3bce0d7c885af71f41da01395d3253dcfc33863306a027a78a7cfad9e"
