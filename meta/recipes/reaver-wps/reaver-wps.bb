SUMMARY = "Brute force attack against Wifi Protected Setup"
HOMEPAGE = "http://code.google.com/p/reaver-wps/"

LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM  = "file://../docs/LICENSE;md5=b8c7f7d7654b933d1bebc68cbdea7c05"

SRCREV = "88"
SRC_URI = "svn://reaver-wps.googlecode.com/svn/;proto=http;module=trunk"

S = "${WORKDIR}/trunk/src"

DEPENDS = "libpcap"

inherit autotools

do_unpackextra() {
        sed -i -e '/^CONFDIR=/d' ${S}/configure.ac
}
addtask unpackextra after do_unpack before do_configure

do_configure_prepend() {
        export CONFDIR='${sysconfdir}'
}

do_install() {
        install -D -p -m 0755 reaver ${D}${bindir}/reaver
        install -d -m 0755 ${D}${sysconfdir}/reaver
}

do_compile_prepend() {
    oe_runmake -C wps CC='${CC} ${CFLAGS}'
    oe_runmake -C libwps CC='${CC} ${CFLAGS}'
    oe_runmake -C lwe CC='${CC} ${CFLAGS} ${LDFLAGS}'
}

RRECOMMENDS_${PN} += "wireless-tools iw"
