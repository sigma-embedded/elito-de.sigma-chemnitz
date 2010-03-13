PLUGINNAME = "${@bb.data.getVar('PN',d,1).replace('vdr-','',1)}"
S = "${WORKDIR}/${PLUGINNAME}-${PV}"

VDR_PLUGINDIR = "${libdir}/vdr"
VDR_CONFIGDIR = "${sysconfdir}/vdr"

DEPENDS += "vdr"
PLUGINS ?= "lib${PN}.so.*"

vdr_runmake() {
    oe_runmake \
	LIBDIR=. LOCALEDIR=./locale VDRDIR=${STAGING_LIBDIR}/vdr \
	STRIP=/bin/true "$@"
}

do_compile() {
    vdr_runmake
}

do_install() {
    install -d -m 0755 ${D}${VDR_PLUGINDIR}
    install -p -m 0755 ${PLUGINS} ${D}${VDR_PLUGINDIR}/

    vdr_runmake i18n LOCALEDIR=${D}${datadir}/locale
}

RPROVIDES_${PN}	       = "vdr-plugin-${PLUGINNAME}"
FILES_${PN}            = "${VDR_PLUGINDIR}/libvdr-*.so*"
FILES_${PN}-dbg       .= " ${VDR_PLUGINDIR}/.debug/*"

inherit gettext
