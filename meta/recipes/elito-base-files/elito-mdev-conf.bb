DESCRIPTION	= "Miscellaneous files for mdev based system."
SECTION		= "base"
PRIORITY	= "required"
PR		= "r4"
LICENSE		= "GPLv3"
PACKAGE_ARCH	= "${MACHINE_ARCH}"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=2c12447f794c304d9cd353f87a432c9e"

SRC_URI		= " \
	file://ubi-name.sh \
	file://mdev.conf \
"

OVERRIDES .= "${@base_contains('MACHINE_FEATURES','ubifs',':ubifs','',d)}"

do_install() {
    install -d ${D}${sysconfdir}/mdev
    install -p -m 0644 ${WORKDIR}/mdev.conf ${D}${sysconfdir}/
}

do_install_append_ubifs() {
    install -p -m 0755 ${WORKDIR}/ubi-name.sh ${D}${sysconfdir}/mdev/
}

FILES_${PN} = "${sysconfdir}/mdev.conf ${sysconfdir}/mdev/*.sh"
CONFFILES_${PN} = "${sysconfdir}/mdev.conf"
