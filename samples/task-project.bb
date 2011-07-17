DESCRIPTION  = "@PROJECT_NAME@ tasks"
LICENSE      = "GPLv3"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=2c12447f794c304d9cd353f87a432c9e"

SRCREV       = "${PROJECT_REVISION}"

PV = "0.1+gitr${SRCPV}"
PR = "r0"

SRC_URI = "git://${PROJECT_TOPDIR};protocol=file"

# for PROJECT_REVISION
inherit elito-utils
inherit task

do_fetch() {
}

do_unpack() {
}

do_distribute_sources() {
}

# !! DO NOT MOVE IT TO TOP !!
# Else, the task class sets PACKAGE_ARCH to all which will override
# value here.
PACKAGE_ARCH = "${MACHINE_ARCH}"

# add recipes which are not catched by normal dep resolving and which
# would result into
#
# |  Collected errors:
# |    * ERROR: Cannot satisfy the following dependencies for elito-task-core:
#
# like errors during rootfs creation.
DEPENDS += "upstart-setup alsa-utils openntpd elito-develcomp"

RDEPENDS_${PN} = "\
	files-@PROJECT_NAME@		\
"

RRECOMMENDS_${PN} = "\
	upstart-setup-syslogd		\
	upstart-setup-klogd		\
	upstart-setup-openntpd		\
	upstart-setup-net-dhcp		\
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
