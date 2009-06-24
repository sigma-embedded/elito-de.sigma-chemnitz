SECTION		 = "base"
PRIORITY	 = "optional"
DESCRIPTION	 = "Event driven system init"
LICENSE		 = "GPL"

inherit autotools native

libdir		 = "/lib"
sbindir		 = "/sbin"
DEPENDS		 = "dbus-native-upstart expat-native bison-native pkgconfig-native"

sbindir		 = "${base_prefix}/sbin"

do_configure() {
	oe_runconf --disable-static
}

do_stage() {
	oe_runmake install
	autotools_stage_all

	mkdir -p ${STAGING_BINDIR}
	libtool --mode=install install -p -m 0755 nih-dbus-tool/nih-dbus-tool ${STAGING_BINDIR}/
}

do_install() {
	:
}
