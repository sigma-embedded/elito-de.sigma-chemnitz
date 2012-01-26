DESCRIPTION	= "Miscellaneous files for systemd based system."
SECTION		= "base"
PRIORITY	= "required"
PV		= "0.2"
PR		= "r8"
LICENSE		= "GPLv3"
PACKAGE_ARCH	= "${MACHINE_ARCH}"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"

SRC_URI		= " \
	file://00-elito.conf \
	file://systemd.profile \
"

do_install[dirs] = "${WORKDIR}"
do_install() {
    install -D -p -m 0644 00-elito.conf ${D}${libdir}/tmpfiles.d/00-elito.conf
    install -D -p -m 0644 systemd.profile ${D}${sysconfdir}/profile.d/systemd.sh
}

FILES_${PN} = "${libdir}/tmpfiles.d/*.conf ${sysconfdir}/profile.d/systemd.sh"
