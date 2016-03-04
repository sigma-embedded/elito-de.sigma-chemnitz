SUMMARY		= "ELiTo release files"
DESCRIPTION	= "${ELITO_BUILDINFO}"
SECTION		= "base"
LICENSE		= "GPLv3"
PV		= "${DISTRO_VERSION}"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"

PACKAGE_ARCH    = "${MACHINE_ARCH}"
FEED_PREFIX    ?= 'elito-'
FEED_BASE_URI  ?= "http://${TFTP_SERVER}:8888/${PROJECT_NAME}/${DISTRO_RELEASE}/ipk"

SRC_URI         = "\
	file://RPM-GPG-KEY-elito-2008	\
	file://issues			\
"

PACKAGES        = "${PN} ${PN}-feeds"

OPKG_FEEDS_PROVIDER  ?= "${PN}-feeds"

FILES_${PN}           = "/etc/pki/elito/* /etc/issues"
RDEPENDS_${PN}        = "elito-filesystem"
RRECOMMENDS_${PN}     = "${OPKG_FEEDS_PROVIDER}"

FILES_${PN}-feeds     = "/etc/opkg/*.conf"

python () {
    import bb.parse
    bb.parse.mark_dependency(d, d.expand("${TMPDIR}/build-info"))
}

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

        for f in feeds/*; do
		test -e "$f" || continue
		install -p -m 0644 "$f"  ${D}/etc/opkg/
        done

	cd ${WORKDIR}
	install -p -m 0444 RPM-GPG-KEY* ${D}/etc/pki/elito/

	sed	\
		-e 's!@DISTRO_NAME@!${DISTRO_NAME}!g'		\
		-e 's!@DISTRO_VERSION@!${DISTRO_VERSION}!g'	\
		issues > ${D}${sysconfdir}/issues
}
