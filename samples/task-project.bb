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

do_fetch[noexec] = "1"
do_unpack[noexec] = "1"
do_patch[noexec] = "1"
do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_install[noexec] = "1"
do_distribute_sources[noexec] = "1"
do_populate_sysroot[noexec] = "1"

OVERRIDES .= ":initsys-${IMAGE_INIT_MANAGER}"

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
DEPENDS += "elito-develcomp alsa-utils"
DEPENDS_initsys-upstart += "upstart-setup"

RDEPENDS_${PN} = "\
	files-@PROJECT_NAME@		\
"

extra-RRECOMMENDS = ""

extra-RRECOMMENDS_initsys-systemd = " \
	busybox-systemd \
	dropbear-systemd \
"

extra-RRECOMMENDS_initsys-upstart = " \
	upstart-setup-fsck-dummy	\
	upstart-setup-klogd		\
	upstart-setup-net-dhcp		\
	upstart-setup-syslogd		\
	upstart-setup-tty-plain		\
"

RRECOMMENDS_${PN} = "\
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
\
	${extra-RRECOMMENDS}		\
"
