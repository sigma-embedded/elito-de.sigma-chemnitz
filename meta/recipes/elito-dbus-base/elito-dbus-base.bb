DESCRIPTION	= "D-Bus base setup"
SECTION		= "base"
PRIORITY	= "required"
LICENSE		= "GPLv3"
PACKAGE_ARCH	= "all"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=2c12447f794c304d9cd353f87a432c9e"

PV		= "0.1"
PR		= "r1"

SRC_URI = " \
	file://01-dbus-root.txt	\
"

do_install() {
	mkdir -p ${D}/root ${D}/var/run/dbus-root ${D}/etc/files.d
	ln -s /var/run/dbus-root ${D}/root/.dbus
	install -p -m 0644 ${WORKDIR}/01-dbus-root.txt ${D}/etc/files.d/
}

PACKAGES = "${PN}"
FILES_${PN} = "/root/.dbus /etc/files.d/01-dbus-root.txt"
