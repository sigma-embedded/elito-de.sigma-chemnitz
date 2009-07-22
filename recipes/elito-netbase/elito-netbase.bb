DESCRIPTION	= "This package provides the necessary \
infrastructure for basic TCP/IP based networking."
SECTION		= "base"
LICENSE		= "GPL"
PR		= "r2"
PV		= 4.21

FILESDIR	= "${OEDEV_TOPDIR}/recipes/netbase/netbase"
DEFAULT_PREFERENCE = 10

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
RPROVIDES_${PN} = "netbase virtual/netbase"
RCONFLICTS	= "netbase"
