SECTION          = "base"
PRIORITY         = "optional"
DESCRIPTION      = "Upstart base library"
LICENSE          = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

base_deps        = "dbus expat pkgconfig-native bison-native"
DEPENDS          = "${base_deps} libnih-native"
DEPENDS_virtclass-native = "${base_deps}"

PV = "1.0.3"
PR = "r0"

SRC_URI = " \
	http://upstart.ubuntu.com/download/libnih/1.0/libnih-${PV}.tar.gz \
	file://libnih-nom4.patch"

SRC_URI[md5sum] = "db7990ce55e01daffe19006524a1ccb0"
SRC_URI[sha256sum] = "897572df7565c0a90a81532671e23c63f99b4efde2eecbbf11e7857fbc61f405"

inherit autotools

_libdir                  = "${base_libdir}"
_libdir_virtclass-native = "${libdir}"

EXTRA_OECONF     = "--disable-static --disable-rpath --libdir=${_libdir}"
EXTRA_AUTORECONF = "--force"

LEAD_SONAME = "libnih.so"

FILES_${PN} = "${_libdir}/*.so.*"
FILES_${PN}-dev  += "${bindir}/nih-dbus-tool ${_libdir}/*.so"

do_install_append() {
    rm -f ${D}${_libdir}/*.la
}

BBCLASSEXTEND = "native"
