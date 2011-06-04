LICENSE = "GPL"
PV = "0.99.6"
PR = "r1"

DEPENDS = "libxine aalib libxinerama libxtst libpng libxft libxv libx11 readline"

SRC_URI = "http://prdownloads.sourceforge.net/xine/${PN}-${PV}.tar.bz2;name=tarball \
	file://configure-hostinclude.patch"

SRC_URI[tarball.md5sum] = "18057dafd2f8422d090a6bef6245652e"
SRC_URI[tarball.sha256sum] = "6c7d30479504154dec102a1431a0632be4083c7ec7dfe6230720a2676aa87f3a"

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
