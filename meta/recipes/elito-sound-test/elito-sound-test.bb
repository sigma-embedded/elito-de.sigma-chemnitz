SECTION  = "base"
DESCRIPTION = "ELiTo Sound Test utilities"
PV = "0.0.2"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"

PACKAGE_ARCH = "all"

SRC_URI = "\
  ${ELITO_MIRROR}/elito-sound-test.tar.xz;name=sounds \
  http://www.gnu.org/music/free-software-song.ogg;name=fssogg \
"

do_install() {
    install -d -m 0755 ${D}${datadir}/sounds
    install -p -m 0644 ${WORKDIR}/*.wav ${WORKDIR}/*.ogg ${D}${datadir}/sounds/
}

PACKAGES = "${PN}"
FILES_${PN} = "${datadir}/sounds/*"


SRC_URI[sounds.md5sum] = "13f8df2fdcefded5331c7656c0d7abb1"
SRC_URI[sounds.sha256sum] = "a126f388ba5a86eec25b61b85c64790f31c6b759b8d0c504546f3f3ed108ea35"

SRC_URI[fssogg.md5sum] = "f95216296abb48f8ef5568982b4f0085"
SRC_URI[fssogg.sha256sum] = "07fee9d4305565fafdedc322187cbe9c16e93feb42242ccdad2b6a831e6c9059"
