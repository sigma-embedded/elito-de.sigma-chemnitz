# --*- python -*--
SECTION		 = "base"
PRIORITY	 = "optional"
DESCRIPTION	 = "Event driven system init"
LICENSE		 = "GPLv2"

inherit autotools autotools_stage native

libdir		 = "/lib"
sbindir		 = "/sbin"
DEPENDS		 = "dbus-native-upstart expat-native bison-native pkgconfig-native"

sbindir		 = "${base_prefix}/sbin"

EXTRA_OECONF	 = "--disable-static"

do_stage_append() {
	mkdir -p ${STAGING_BINDIR}
	libtool --mode=install install -p -m 0755 nih-dbus-tool/nih-dbus-tool ${STAGING_BINDIR}/
}

do_install() {
	:
}
