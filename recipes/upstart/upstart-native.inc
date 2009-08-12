# --*- python -*--
SECTION          = "base"
PRIORITY         = "optional"
DESCRIPTION      = "Event driven system init"
LICENSE          = "GPLv2"
DEPENDS          = "dbus-native (>=1.2.16) expat-native \
                    bison-native pkgconfig-native"

inherit autotools autotools_stage native

libdir           = "/lib"
sbindir          = "${base_prefix}/sbin"

EXTRA_OECONF     = "--disable-static"

do_stage_append() {
	mkdir -p ${STAGING_BINDIR}
	libtool --mode=install install -p -m 0755 nih-dbus-tool/nih-dbus-tool ${STAGING_BINDIR}/
}

do_install() {
	:
}
