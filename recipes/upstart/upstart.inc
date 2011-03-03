# --*- python -*--
SECTION          = "base"
PRIORITY         = "optional"
DESCRIPTION      = "Event driven system init"
LICENSE          = "GPLv2"
DEPENDS          = "dbus expat bison-native pkgconfig-native libnih libnih-native"

PV = "1.0"
PR = "r0"

SRC_URI = "	\
        http://launchpad.net/upstart/1.x/1.0/+download/upstart-${PV}.tar.gz \
	file://upstart-0.5-oomadj.patch				\
	file://old-glibc.patch					\
"

SRC_URI[md5sum] = "bbcdf7580d5cd35b4b1da44bcb12de9f"
SRC_URI[sha256sum] = "0eddebcb87c9493206ccdcae79d5e1297b2fb3330b10ae98252898c53602c616"

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
