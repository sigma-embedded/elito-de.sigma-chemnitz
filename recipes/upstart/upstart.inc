# --*- python -*--
SECTION          = "base"
PRIORITY         = "optional"
DESCRIPTION      = "Event driven system init"
LICENSE          = "GPLv2"
DEPENDS          = "dbus expat bison-native pkgconfig-native libnih libnih-native"

PV = "1.2"
PR = "r0"

SRC_URI = "	\
        http://launchpad.net/upstart/1.x/${PV}/+download/upstart-${PV}.tar.gz \
	file://upstart-0.5-oomadj.patch				\
	file://old-glibc.patch					\
"

SRC_URI[md5sum] = "c1b1bab5c1c1ed2595081d8178542b37"
SRC_URI[sha256sum] = "df56f1f8638c6cc2202c9ba5f8a1d093f091303c58ad3bce6a375f9ca0821710"

inherit autotools

EXTRA_OECONF     = "--disable-static --disable-rpath \
	--libdir=${base_libdir} --sbindir=${base_sbindir}"
EXTRA_AUTORECONF = "--force"

do_compile() {
	oe_runmake check_PROGRAMS=
}

do_install_append() {
	mv ${D}${base_sbindir}/init ${D}${base_sbindir}/init.upstart
}

RDEPENDS_${PN}	+= "update-alternatives"
pkg_postinst_${PN}() {
	update-alternatives --install ${base_sbindir}/init.wrapped init-wrapped ${base_sbindir}/init.upstart 10
}

pkg_postrm_${PN}() {
	update-alternatives --remove init-wrapped ${base_sbindir}/init.upstart
}
