DESCRIPTION  = "@PROJECT_NAME@ tasks"
LICENSE      = "GPLv3"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"

PV = "0.2"
PR = "r0+b${ELITO_BUILD_NUMBER}"

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
DEPENDS += "elito-develcomp"
DEPENDS_initsys-upstart += "upstart-setup"

RDEPENDS_${PN} = "\
	files-@PROJECT_NAME@		\
"

extra-RRECOMMENDS = ""

extra-RRECOMMENDS_initsys-systemd = " \
	connman-systemd \
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
	kernel-module-ehci-hcd		\
	kernel-module-evdev		\
	kernel-module-i2c-dev		\
	kernel-module-iptable-filter	\
	kernel-module-iptable-nat	\
	kernel-module-mtdblock		\
	kernel-module-mtdblock-ro	\
	kernel-module-mtdchar		\
	kernel-module-nls-ascii		\
	kernel-module-nls-cp437		\
	kernel-module-nls-cp850		\
	kernel-module-nls-iso8859-1	\
	kernel-module-nls-iso8859-15	\
	kernel-module-nls-utf8		\
	kernel-module-usb-serial	\
	kernel-module-usb-storage	\
	kernel-module-usbhid		\
	kernel-module-vfat		\
\
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
