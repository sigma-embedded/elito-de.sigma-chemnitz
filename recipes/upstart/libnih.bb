SECTION          = "base"
PRIORITY         = "optional"
DESCRIPTION      = "Upstart base library"
LICENSE          = "GPLv2"

base_deps        = "dbus expat pkgconfig-native bison-native"
DEPENDS          = "${base_deps} libnih-native"
DEPENDS_virtclass-native = "${base_deps}"

PV = "1.0.1"
PR = "r7"

SRC_URI = " \
	http://upstart.ubuntu.com/download/libnih/1.0/libnih-${PV}.tar.gz;name=tarball \
	file://libnih-nom4.patch;patch=1"

SRC_URI[tarball.md5sum]    = "3e410e32a51b4e6124547c2ced308efc"
SRC_URI[tarball.sha256sum] = "ba1d0dcdbc5e2eefa47d1e84cafcccc0e0ab0f5967a9b508da79a7e9f38553ea"

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
