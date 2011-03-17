# --*- python -*--
SECTION          = "base"
PRIORITY         = "optional"
DESCRIPTION      = "Event driven system init"
LICENSE          = "GPLv2"
DEPENDS          = "dbus expat bison-native pkgconfig-native libnih libnih-native"

PV = "1.1"
PR = "r0"

SRC_URI = "	\
        http://launchpad.net/upstart/1.x/${PV}/+download/upstart-${PV}.tar.gz \
	file://upstart-0.5-oomadj.patch				\
	file://old-glibc.patch					\
"

SRC_URI[md5sum] = "9e9ce9d5e3936e59331f478d8a436e00"
SRC_URI[sha256sum] = "1e246b31fd0533d594afa357e54a851db6747a255534364570336233564350bf"

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
