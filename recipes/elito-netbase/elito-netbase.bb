DESCRIPTION	= "This package provides the necessary \
infrastructure for basic TCP/IP based networking."
SECTION		= "base"
LICENSE		= "GPL"
PR		= "r3"
PV		= 4.41

FILESDIR	= "${OEDEV_TOPDIR}/recipes/netbase/netbase"
DEFAULT_PREFERENCE = 10

SRC_URI		= "${DEBIAN_MIRROR}/main/n/netbase/netbase_${PV}.tar.gz"
SRC_URI_OVERRIDES_PACKAGE_ARCH = "1"

SRC_URI[md5sum] = "58255f1a729795a0c34197f223b1a155"
SRC_URI[sha256sum] = "64e34618a488d3e7f2e32d0172e31af060e7495214ac2c5647d0ecf30f94d0e0"

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
