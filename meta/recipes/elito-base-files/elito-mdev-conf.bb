DESCRIPTION	= "Miscellaneous files for mdev based system."
SECTION		= "base"
PRIORITY	= "required"
PV		= "1.1"
LICENSE		= "GPLv3"
PACKAGE_ARCH	= "${MACHINE_ARCH}"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"

SRC_URI		= " \
	file://ubi-name.sh \
	file://mdev.conf \
"

OVERRIDES .= "${@bb.utils.contains('MACHINE_FEATURES','ubifs',':ubifs','',d)}"

do_install() {
    install -d ${D}${sysconfdir}/mdev
    install -p -m 0644 ${WORKDIR}/mdev.conf ${D}${sysconfdir}/
}

do_install_append_ubifs() {
    install -p -m 0755 ${WORKDIR}/ubi-name.sh ${D}${sysconfdir}/mdev/
}

FILES_${PN} = "${sysconfdir}/mdev.conf ${sysconfdir}/mdev/*.sh"
CONFFILES_${PN} = "${sysconfdir}/mdev.conf"
