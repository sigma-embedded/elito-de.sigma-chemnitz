DESCRIPTION  = "@PROJECT_NAME@ tasks"
LICENSE      = "proprietary"
SRCREV       = "${PROJECT_REVISION}"

PV = "0.1+gitr${SRCPV}"
PR = "r0"

SRC_URI = "git://${PROJECT_TOPDIR};protocol=file"

do_fetch() {
}

do_unpack() {
}

inherit task

# !! DO NOT MOVE IT TO TOP !!
# Else, the task class sets PACKAGE_ARCH to all which will override
# value here.
PACKAGE_ARCH = "${MACHINE_ARCH}"

DEPENDS += "upstart-setup alsa-utils"

RDEPENDS_${PN} = "\
	files-@PROJECT@			\
"

RRECOMMENDS_${PN} = "\
	upstart-setup-syslogd		\
	upstart-setup-klogd		\
	upstart-setup-openntpd		\
	upstart-setup-net-dhcp-eth0	\
	alsa-utils-alsamixer		\
	alsa-utils-amixer		\
	alsa-utils-aplay		\
	alsa-utils-alsactl		\
	elito-fbtest			\
	strace				\
	minicom				\
	i2c-tools			\
	tcpdump				\
	fbset				\
	dropbear			\
	opkg				\
	usbutils			\
"
