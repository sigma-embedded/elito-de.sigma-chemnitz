DESCRIPTION = "Open-source JPEG 2000 codec written in C language"
HOMEPAGE = "http://www.openjpeg.org"
SECTION = "libs"
LICENSE = "BSD"
BBCLASSEXTEND = "native"
LIC_FILES_CHKSUM = "file://LICENSE;md5=37fa58b254f513a528201d5a8cfa9d35"

DEPENDS = "zlib libpng tiff libxext"

SRC_URI = "http://openjpeg.googlecode.com/files/openjpeg_v1_4_sources_r697.tgz \
  file://libm.patch"

S = "${WORKDIR}/openjpeg_v1_4_sources_r697"

inherit cmake

EXTRA_OECMAKE="-DBUILD_SHARED_LIBS:BOOL=ON"

PACKAGES =+ "openjpeg-tools "
FILES_openjpeg-tools = "${bindir}/*"

SRC_URI[md5sum] = "7870bb84e810dec63fcf3b712ebb93db"
SRC_URI[sha256sum] = "493e4db087bf5c5485618a1e1cfd827c4fb2ad421a4342903b166b76cf094b73"
