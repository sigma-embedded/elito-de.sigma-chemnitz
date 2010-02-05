# --*- python -*--
SECTION          = "base"
PRIORITY         = "optional"
DESCRIPTION      = "Event driven system init"
LICENSE          = "GPLv2"
DEPENDS          = "dbus expat bison-native pkgconfig-native libnih libnih-native"

PV = "0.6.5"

SRC_URI = "	\
	http://upstart.ubuntu.com/download/0.6/upstart-${PV}.tar.gz;name=tarball \
	file://upstart-0.5-oomadj.patch;patch=1				\
	file://old-glibc.patch;patch=1					\
"

SRC_URI[tarball.md5sum] = ""
SRC_URI[tarball.sha256sum] = ""

inherit autotools

EXTRA_OECONF     = "--disable-static --disable-rpath --libdir=/lib --sbindir=/sbin"
EXTRA_AUTORECONF = "--force"

do_configure_prepend_dyn() {
	export nih_install=yes
}

do_configure_prepend() {
	NIH_DBUS_TOOL=`which nih-dbus-tool`
	sed -i \
            -e "s!\$(builddir)/nih-dbus-tool!${NIH_DBUS_TOOL}!g" \
            -e "s!\$(top_builddir)/nih-dbus-tool/nih-dbus-tool!${NIH_DBUS_TOOL}!g" \
            */Makefile.*
}

do_compile() {
	oe_runmake check_PROGRAMS=
}

do_install_append() {
	mv ${D}/sbin/init ${D}/sbin/init.upstart
        rm -f ${D}${datadir}/aclocal/*.m4
}

RDEPENDS_${PN}	+= "update-alternatives"
pkg_postinst_${PN}() {
	update-alternatives --install /sbin/init.wrapped init-wrapped /sbin/init.upstart 10
}

pkg_postrm_${PN}() {
	update-alternatives --remove init-wrapped /sbin/init.upstart
}
