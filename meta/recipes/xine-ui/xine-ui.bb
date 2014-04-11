LICENSE = "GPL"
PV = "0.99.8"

DEPENDS = "libxine aalib libxinerama libxtst libpng libxft libxv libx11 readline"

SRC_URI = "${SOURCEFORGE_MIRROR}/xine/xine-ui/${PV}/${PN}-${PV}.tar.xz;name=tarball \
	file://configure-hostinclude.patch"

SRC_URI[tarball.md5sum] = "8dc079baeab56c99e33268a973bc288e"
SRC_URI[tarball.sha256sum] = "1ac5a9fc8dbf81f12a8677108bf2b623e70d7adbb83b83912b9cdf00bc78b2a7"

inherit autotools

EXTRA_OECONF = "--disable-lirc --without-curl --with-readline=${STAGING_INCDIR}/.."

PACKAGES =+ "${PN}-xtras ${PN}-fb ${PN}-skins-xinetic"

FILES_${PN}      += "\
	${datadir}/mime/packages/xine-ui.xml \
	${datadir}/xine/desktop/xine.desktop \
	${datadir}/icons/hicolor/*/apps/xine.png \
	${datadir}/xine/skins \
"
FILES_${PN}-xtras = "${bindir}/xine-check ${bindir}/xine-bugreport"
FILES_${PN}-fb    = "${bindir}/fbxine"
FILES_${PN}-skins-xinetic = "${datadir}/xine/skins/xinetic"

RDEPENDS_${PN} = "virtual/xine-skin"
RPROVIDES_${PN}-skins-xinetic = "virtual/xine-skin"
