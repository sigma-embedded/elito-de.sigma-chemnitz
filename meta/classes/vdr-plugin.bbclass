PLUGINNAME = "${@bb.data.getVar('PN',d,1).replace('vdr-','',1)}"
S = "${WORKDIR}/${PLUGINNAME}-${PV}"

VDR_PLUGINDIR = "${libdir}/vdr"
VDR_CONFIGDIR = "${localstatedir}/lib/vdr/etc"

DEPENDS += "vdr"

VDR_LEGACY_PLUGIN ?= "false"
VDR_LEGACY_PLUGIN[type] = "boolean"

OVERRIDES .= "${@['', ':vdr-legacy'][oe.data.typed_value('VDR_LEGACY_PLUGIN', d)]}"

do_configure_prepend_vdr-legacy() {
    mkdir -p .vdr-hack .vdr-lib
    cat << "EOF" > .vdr-hack/Make.config

CFLAGS =	$(shell pkg-config --variable=cflags vdr)
CXXFLAGS =	$(shell pkg-config --variable=cxxflags vdr)
LIBDIR =    	${S}/.vdr-lib

XAPIVERSION =	$(shell pkg-config --variable=apiversion vdr)
XVDRVERSION =	$(shell pkg-config --modversion vdr)

export CFLAGS CXXFLAGS LIBDIR
EOF
}

vdr_runmake_vdr-legacy() {
    unset CFLAGS CXXFLAGS
    oe_runmake STRIP=/bin/true \
	APIVERSION='$(XAPIVERSION)' VDRVERSION='$(XVDRVERSION)' \
	VDRDIR='${S}/.vdr-hack' \
	"$@"
}

do_install_vdr-legacy() {
    vdr_runmake i18n LOCALEDIR=${D}${datadir}/locale

    cd .vdr-lib
    for i in *.so.*; do
        install -D -p -m 0755 $i ${D}${VDR_PLUGINDIR}/$i
    done
    cd -
}


vdr_runmake() {
    unset CFLAGS CXXFLAGS
    oe_runmake STRIP=/bin/true "$@"
}

do_compile() {
    vdr_runmake all
}

do_install() {
    vdr_runmake install DESTDIR="${D}"
}

RPROVIDES_${PN}	       = "vdr-plugin-${PLUGINNAME}"
FILES_${PN}            = "${VDR_PLUGINDIR}/libvdr-*.so.1.*"
FILES_${PN}-dbg       .= " ${VDR_PLUGINDIR}/.debug/*"

inherit gettext elito-normalize-po
