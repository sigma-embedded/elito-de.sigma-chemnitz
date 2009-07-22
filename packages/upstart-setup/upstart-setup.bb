SECTION		= "base"
DESCRIPTION	= "upstart base setup"
LICENSE		= "GPLv3"
PV		= "0.4.2"
PR		= "r1"
PACKAGE_ARCH	=  "all"

SRC_URI		= "		\
	${ELITO_MIRROR}/${PN}-${PV}.tar.bz2			\
	file://paths						\
"
SRCREV		 = "${AUTOREV}"
#S		 = "${WORKDIR}/git"

PACKAGES	 = "${PN}-base ${PN}-udev ${PN}-net ${PN}-net-dhcp	\
	${PN}-openntpd ${PN}-dropbear ${PN}-dbus	\
	${PN}-tty-plain ${PN}-syslogd ${PN}-klogd	\
	${PN}-sound ${PN}-rtc-sync\
	${PN}-net-dhcp-eth0	\
	${PN}-net-dhcp-eth1	\
	${PN}-empty \
"

j		 = "${sysconfdir}/init/"


# TODO: move udev rules and files.d into own package
FILES_${PN}-base = "				\
	${j}ctrl-alt-del.conf			\
	${j}halt.conf				\
	${j}init/boot.conf			\
	${j}init/dmesg.conf			\
	${j}init/mount-all.conf			\
	${j}init/mount-tmpfs.conf		\
	${j}init/ldconfig.conf			\
	${j}shutdown/bind-mounts.conf		\
	${j}shutdown/shutdown.conf		\
	${sysconfdir}/files.d/*-log.txt		\
	/lib/udev/rules.d/*-upstart.rules					\
"
FILES_${PN}-base_append_arm += "${j}init/cpu-align.conf"
RDEPENDS_${PN}-base         += "upstart elito-setup-tools virtual/upstart-tty"
RPROVIDES_${PN}-base         = "virtual/initscripts"

FILES_${PN}-udev	 = "		\
	${j}init/udev-fill.conf		\
	${j}init/udev-early.conf	\
	${j}services/udevd.conf"
RDEPENDS_${PN}-udev = "udev"
RPROVIDES_${PN}-udev = "virtual/modutils-initscripts"


FILES_${PN}-net		 = "	\
	/sbin/upstart-if-down	\
	/sbin/upstart-if-up	\
	${j}network/add-lo.conf"

FILES_${PN}-net-dhcp	 = " \
	${j}network/net-dhcp-deconfig.conf	\
	${j}network/net-dhcp-renew.conf		\
	${j}network/dhcp-up.conf		\
	/share/udhcpc/upstart.script"
RDEPENDS_${PN}-net-dhcp = "${PN}-net busybox"

FILES_${PN}-net-dhcp-eth0 = "${j}network/add-eth0-dhcp.conf"
RDEPENDS_${PN}-net-dhcp-eth0 = "${PN}-net-dhcp"

FILES_${PN}-net-dhcp-eth1 = "${j}network/add-eth1-dhcp.conf"
RDEPENDS_${PN}-net-dhcp-eth1 = "${PN}-net-dhcp"

FILES_${PN}-openntpd = " \
	/sbin/upstart-ntpd-up			\
	${j}services/openntpd.conf		\
	${j}network/net-dhcp-ntp-renew.conf"
RDEPENDS_${PN}-openntpd = "${PN}-net openntpd"

FILES_${PN}-dropbear = " \
	${j}init/dropbear.conf		\
	${j}services/dropbear.conf"
RDEPENDS_${PN}-dropbear   = "dropbear"
RPROVIDES_${PN}-dropbear  = "virtual/dropbear-init"

FILES_${PN}-rtc-sync = " \
	${j}init/rtc-sync.conf		\
	${j}shutdown/rtc-sync.conf	\
"

FILES_${PN}-tty-plain     = "${j}tty/plain.conf"
RPROVIDES_${PN}-tty-plain = "virtual/upstart-tty"

FILES_${PN}-syslogd = "${j}services/syslogd.conf"
RPROVIDES_${PN}-syslogd = "virtual/syslogd-init"
RDEPENDS_${PN}-syslogd = "busybox"

FILES_${PN}-klogd = "${j}services/klogd.conf"
RPROVIDES_${PN}-klogd = "virtual/klogd-init"
RDEPENDS_${PN}-klogd = "busybox"

FILES_${PN}-sound = "${j}init/sound.conf"
RPROVIDES_${PN}-sound = "virtual/sound-init"
RDEPENDS_${PN}-sound = "alsa-utils-alsactl"

FILES_${PN}-dbus = "\
	${j}services/dbus-daemon.conf	\
	${j}init/dbus-uuidgen.conf	\
	${sysconfdir}/files.d/*-dbus.txt"
RPROVIDES_${PN}-dbus = "virtual/dbus-init"
RDEPENDS_${PN}-dbus = "dbus"

RPROVIDES_${PN}-empty = "virtual/tinylogin-init"

do_compile() {
}

do_install() {
	mkdir -p ${D}${sysconfdir} ${D}/sbin ${D}/share/udhcpc ${D}/lib/udev/rules.d ${D}${sysconfdir}/files.d
	cp -a jobs.d ${D}${sysconfdir}/init

	install -p -m 0755 upstart-if-down upstart-if-up ${D}/sbin/
	install -p -m 0755 upstart-ntpd-up               ${D}/sbin/
	install -p -m 0755 upstart.script                ${D}/share/udhcpc/

	install -p -m 0644 [0-9][0-9]-*.rules            ${D}/lib/udev/rules.d/
	install -p -m 0644 [0-9][0-9]-*.txt              ${D}${sysconfdir}/files.d/

	find ${D} -type f -print0 | xargs -0r ${S}/fix-paths ${WORKDIR}/paths
}
