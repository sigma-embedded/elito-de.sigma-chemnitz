# --*- python -*--
SECTION          = "base"
PRIORITY         = "optional"
DESCRIPTION      = "Event driven system init"
LICENSE          = "GPLv2"
DEPENDS          = "dbus expat bison-native pkgconfig-native libnih libnih-native"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

PV = "1.3"
PR = "r1"

SRC_URI = "	\
        http://launchpad.net/upstart/1.x/${PV}/+download/upstart-${PV}.tar.gz \
	file://old-glibc.patch					\
"

SRC_URI[md5sum] = "7820797b64878c27115fff6a7398a6a9"
SRC_URI[sha256sum] = "27d68586e1a1d98c281738b0972db92fe4522725f5df12790f237f89e64315e4"

inherit autotools gettext

EXTRA_OECONF     = "--disable-static --disable-rpath \
	--libdir=${base_libdir} --sbindir=${base_sbindir}"
EXTRA_AUTORECONF = "--force"

RPROVIDES_${PN} += "virtual/initctl"

do_compile() {
	oe_runmake check_PROGRAMS=
}

do_install_append() {
	mv ${D}${base_sbindir}/init ${D}${base_sbindir}/init.upstart

        cd ${D}${mandir}/man7
	for i in *.7; do
		test -L "$i" || continue
		d=`readlink "$i"`
		rd=${d##${mandir}/}
		test x"$rd" != x"$d" || continue
		ln -sf "../$rd" "$i"
	done
        cd -
}

RDEPENDS_${PN}	+= "update-alternatives"
pkg_postinst_${PN}() {
	update-alternatives --install ${base_sbindir}/init.wrapped init-wrapped ${base_sbindir}/init.upstart 10
}

pkg_postrm_${PN}() {
	update-alternatives --remove init-wrapped ${base_sbindir}/init.upstart
}
