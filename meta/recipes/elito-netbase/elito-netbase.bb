DESCRIPTION	= "This package provides the necessary \
infrastructure for basic TCP/IP based networking."
SECTION		= "base"
LICENSE		= "GPLv2"
PR		= "r3"
PV		= "4.45"
LIC_FILES_CHKSUM = "file://debian/copyright;md5=3dd6192d306f582dee7687da3d8748ab"

FILESDIR	= "${OECORE_TOPDIR}/meta/recipes-core/netbase/netbase-${PV}"
DEFAULT_PREFERENCE = "10"

SRC_URI		= "${DEBIAN_MIRROR}/main/n/netbase/netbase_${PV}.tar.gz"
SRC_URI_OVERRIDES_PACKAGE_ARCH = "1"

S = "${WORKDIR}/netbase-${PV}"
do_install () {
	install -d ${D}${sysconfdir}
	install -p -m 0644 etc-rpc       ${D}${sysconfdir}/rpc
	install -p -m 0644 etc-protocols ${D}${sysconfdir}/protocols
	install -p -m 0644 etc-services  ${D}${sysconfdir}/services
}

PACKAGES        = "${PN}"
CONFFILES_${PN} = "${sysconfdir}/rpc ${sysconfdir}/protocols \
	${sysconfdir}/services"
RPROVIDES_${PN} = "netbase"
RCONFLICTS	= "netbase"

SRC_URI[md5sum] = "a56cb362ece358a5b3b8972e5c7be534"
SRC_URI[sha256sum] = "dea29d60b3d751f0c5669b5299af312ad269c48e6440e0072d0d68d9544ebad3"
