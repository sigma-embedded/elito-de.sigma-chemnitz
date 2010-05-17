SECTION          = "base"
PRIORITY         = "optional"
DESCRIPTION      = "Upstart base library"
LICENSE          = "GPLv2"

base_deps        = "dbus expat pkgconfig-native bison-native"
DEPENDS          = "${base_deps} libnih-native"
DEPENDS_virtclass-native = "${base_deps}"

PV = "1.0.2"
PR = "r0"

SRC_URI = " \
	http://upstart.ubuntu.com/download/libnih/1.0/libnih-${PV}.tar.gz;name=tarball \
	file://libnih-nom4.patch;patch=1"

SRC_URI[tarball.md5sum]    = "89bf20db4ff3f005cc37482a4f050650"
SRC_URI[tarball.sha256sum] = "7b0c0a95eb718ad0aa591f67db7b9f274cecdedd2037fc066037ea7b99c2257e"

inherit autotools autotools_stage

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
