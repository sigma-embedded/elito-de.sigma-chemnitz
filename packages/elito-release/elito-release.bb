DESCRIPTION	= "ELiTo release files"
SECTION		= "base"
LICENSE		= "GPL"
PR		= "r1"
PV		= "${DISTRO_VERSION}"

PACKAGE_ARCH    = "${MACHINE_ARCH}"
FEED_PREFIX    ?= 'elito-'
FEED_BASE_URI  ?= ''

SRC_URI         = "\
	file://RPM-GPG-KEY-elito-2008	\
	file://issues			\
"

PACKAGES        = "${PN} ${PN}-feeds"

FILES_${PN}           = "/etc/pki/elito/* /etc/issues"
RDEPENDS_${PN}        = "elito-filesystem"
RRECOMMENDS_${PN}     = "virtual/opkg-feeds"
RPROVIDES_${PN}       = "virtual/release-files"

FILES_${PN}-feeds     = "/etc/opkg/*.conf"
RPROVIDES_${PN}-feeds = "virtual/opkg-feeds"

do_compile() {
	set -x
	rm -rf feeds
	mkdir -p feeds

        test -z "${FEED_BASE_URI}" || \
	for arch in ${PACKAGE_ARCHS}; do
		echo "src/gz ${FEED_PREFIX}$arch ${FEED_BASE_URI}/$arch" >> feeds/${FEED_PREFIX}feeds.conf
	done
}

do_install() {
	set -x
	mkdir -p ${D}/etc/opkg/ ${D}/etc/pki/elito

	install -p -m 0644 feeds/* ${D}/etc/opkg/

	cd ${WORKDIR}
	install -p -m 0444 RPM-GPG-KEY* ${D}/etc/pki/elito/

	sed	\
		-e 's!@DISTRO_NAME@!${DISTRO_NAME}!g'		\
		-e 's!@DISTRO_VERSION@!${DISTRO_VERSION}!g'	\
		issues > ${D}${sysconfdir}/issues
}
