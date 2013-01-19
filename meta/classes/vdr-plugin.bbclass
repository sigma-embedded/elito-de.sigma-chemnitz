PLUGINNAME = "${@bb.data.getVar('PN',d,1).replace('vdr-','',1)}"
S = "${WORKDIR}/${PLUGINNAME}-${PV}"

VDR_DEV_DVBBASE ?= "/dev/bus/dvb/"

VDR_PLUGINDIR = "${libdir}/vdr"
VDR_CONFIGDIR = "${localstatedir}/lib/vdr/etc"

DEPENDS += "vdr"
PLUGINS ?= "lib${PN}.so.*"

vdr_fixldflags() {
    cat <<"EOF" >> "$1"
CXXFLAGS = $(CXXFLAGS_) $(CPPFLAGS_)
CFLAGS = $(CFLAGS_) $(CPPFLAGS_)

%.so:	CXXFLAGS:=$(CXXFLAGS) $(LDFLAGS)
%.so:	CFLAGS:=$(CFLAGS) $(LDFLAGS)
EOF
}

do_fixldflags() {
    vdr_fixldflags Makefile
}

addtask fixldflags after do_patch before do_compile

vdr_runmake() {
    CPPFLAGS_='-DDEV_DVBBASE=\"${VDR_DEV_DVBBASE}\"'
    eval CXXFLAGS_=\$\{CXXFLAGS\}\\ -fPIC
    eval CFLAGS_=\$\{CFLAGS\}\\ -fPIC
    unset CXXFLAGS CFLAGS
    export CXXFLAGS_ CFLAGS_ CPPFLAGS_

    oe_runmake \
	LIBDIR=. LOCALEDIR=./locale VDRDIR=${STAGING_LIBDIR}/vdr \
	STRIP=/bin/true "$@"
}

do_compile() {
    vdr_runmake all
}

do_install() {
    install -d -m 0755 ${D}${VDR_PLUGINDIR}
    install -p -m 0755 ${PLUGINS} ${D}${VDR_PLUGINDIR}/

    vdr_runmake i18n LOCALEDIR=${D}${datadir}/locale
}

RPROVIDES_${PN}	       = "vdr-plugin-${PLUGINNAME}"
FILES_${PN}            = "${VDR_PLUGINDIR}/libvdr-*.so.1.*"
FILES_${PN}-dbg       .= " ${VDR_PLUGINDIR}/.debug/*"

inherit gettext elito-normalize-po
