SECTION		 = "base"
PRIORITY	 = "optional"
DESCRIPTION	 = "Event driven system init"
LICENSE		 = "GPL"

inherit autotools

libdir		 = "/lib"
sbindir		 = "/sbin"
DEPENDS		 = "dbus bison-native pkgconfig-native python-native"

do_configure_prepend_dyn() {
	set -x
	export nih_install=yes
}
	
do_configure() {
	oe_runconf --disable-static
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
