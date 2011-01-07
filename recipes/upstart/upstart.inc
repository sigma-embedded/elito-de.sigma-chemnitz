# --*- python -*--
SECTION          = "base"
PRIORITY         = "optional"
DESCRIPTION      = "Event driven system init"
LICENSE          = "GPLv2"
DEPENDS          = "dbus expat bison-native pkgconfig-native libnih libnih-native"

PV = "0.6.7"
PR = "r0"

SRC_URI = "	\
	http://upstart.ubuntu.com/download/0.6/upstart-${PV}.tar.gz;name=tarball \
	file://upstart-0.5-oomadj.patch				\
	file://old-glibc.patch					\
"

SRC_URI[tarball.md5sum] = "37740f81b6d12a53072603fadee451c9"
SRC_URI[tarball.sha256sum] = "29d38ebf126cc601ace3852ca13a967905bd56b1d385189937b12976846e37b8"

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
