DESCRIPTION	= "Miscellaneous files for mdev based system."
SECTION		= "base"
PRIORITY	= "required"
PR		= "r0"
LICENSE		= "GPLv3"
PACKAGE_ARCH	= "${MACHINE_ARCH}"

SRC_URI		= "file://mdev.conf"

do_install() {
    install -d ${D}${sysconfdir}
    install -p -m 0644 ${WORKDIR}/mdev.conf ${D}${sysconfdir}
}

FILES_${PN} = "${sysconfdir}/mdev.conf"
