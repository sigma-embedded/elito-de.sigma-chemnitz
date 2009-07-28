# --*- python -*--
SECTION		 = "base"
PRIORITY	 = "optional"
DESCRIPTION	 = "Event driven system init"
LICENSE		 = "GPLv2"

inherit autotools

libdir		 = "/lib"
sbindir		 = "/sbin"
DEPENDS		 = "dbus expat bison-native pkgconfig-native upstart-native"

EXTRA_OECONF	 = "--disable-static --disable-rpath"
EXTRA_AUTORECONF = "--force"

do_configure_prepend_dyn() {
	set -x
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
}

RDEPENDS_${PN}	+= "update-alternatives"
pkg_postinst_${PN}() {
	update-alternatives --install /sbin/init.wrapped init-wrapped /sbin/init.upstart 10
}

pkg_postrm_${PN}() {
	update-alternatives --remove init-wrapped /sbin/init.upstart
}
