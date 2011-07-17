DESCRIPTION = "Library for reading mod-like audio files."
HOMEPAGE = "http://modplug-xmms.sf.net"
SECTION = "libs"
LICENSE = "GPL"
LIC_FILES_CHKSUM = "file://COPYING;md5=c9182faa1f7c316f7b97d404bcbe3685"

PR = "r1"

SRC_URI = "${SOURCEFORGE_MIRROR}/modplug-xmms/libmodplug-${PV}.tar.gz"

inherit autotools pkgconfig

# NOTE: autotools_stage_all does nothing here, we need to do it manually
do_install_append() {
	install -d ${D}${includedir}/libmodplug
	install -m 0644 ${S}/src/modplug.h ${D}${includedir}/libmodplug
	install -m 0644 ${S}/src/modplug.h ${D}${includedir}/
}
