DESCRIPTION	= "Miscellaneous files for systemd based system."
SECTION		= "base"
PRIORITY	= "required"
PV		= "0.2"
PR		= "r4"
LICENSE		= "GPLv3"
PACKAGE_ARCH	= "${MACHINE_ARCH}"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=2c12447f794c304d9cd353f87a432c9e"

SRC_URI		= " \
	file://00-elito.conf \
"

do_install() {
    install -d ${D}${libdir}/tmpfiles.d
    install -p -m 0644 ${WORKDIR}/00-elito.conf ${D}${libdir}/tmpfiles.d/
}

FILES_${PN} = "${libdir}/tmpfiles.d/*.conf"
