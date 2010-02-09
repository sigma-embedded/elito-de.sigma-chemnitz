# --*- python -*--
SECTION          = "base"
PRIORITY         = "optional"
DESCRIPTION      = "Upstart base library"
LICENSE          = "GPLv2"
DEPENDS          = "dbus expat pkgconfig-native bison-native"

PV = "1.0.1"
PR = "r3"

SRC_URI = " \
	http://upstart.ubuntu.com/download/libnih/1.0/libnih-${PV}.tar.gz;name=tarball"

SRC_URI[tarball.md5sum]    = "3e410e32a51b4e6124547c2ced308efc"
SRC_URI[tarball.sha256sum] = "ba1d0dcdbc5e2eefa47d1e84cafcccc0e0ab0f5967a9b508da79a7e9f38553ea"

inherit autotools autotools_stage

_libdir                  = "${base_libdir}"
_libdir_pn-libnih-native = "${libdir}"

EXTRA_OECONF     = "--disable-static --disable-rpath --libdir=${_libdir}"
EXTRA_AUTORECONF = "--force"

LEAD_SONAME = "libnih.so"

do_install_append() {
    rm -f ${D}${_libdir}/*.la
    rm -f ${D}${datadir}/aclocal/*
}
