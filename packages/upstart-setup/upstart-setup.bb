SECTION		= "base"
DESCRIPTION	= "upstart base setup"
LICENSE		= "GPLv3"
PV		= "0.3.3"
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

j		 = "${sysconfdir}/init/jobs.d/"


# TODO: move udev rules and files.d into own package
FILES_${PN}-base = "				\
	${j}ctrl-alt-del ${j}halt		\
	${j}init/boot ${j}init/dmesg ${j}init/mount-all ${j}init/mount-tmpfs	\
	${j}init/ldconfig							\
	${j}shutdown/bind-mounts ${j}shutdown/shutdown				\
	${sysconfdir}/files.d/*-log.txt						\
	/lib/udev/rules.d/*-upstart.rules					\
"
FILES_${PN}-base_append_arm += "${j}init/cpu-align"
RDEPENDS_${PN}-base         += "upstart elito-setup-tools virtual/upstart-tty"
RPROVIDES_${PN}-base         = "virtual/initscripts"

FILES_${PN}-udev	 = "	\
	${j}init/udev-fill	\
	${j}init/udev-early	\
	${j}services/udevd"
RDEPENDS_${PN}-udev = "udev"
RPROVIDES_${PN}-udev = "virtual/modutils-initscripts"


FILES_${PN}-net		 = "	\
	/sbin/upstart-if-down	\
	/sbin/upstart-if-up	\
	${j}network/add-lo"

FILES_${PN}-net-dhcp	 = " \
	${j}network/net-dhcp-deconfig		\
	${j}network/net-dhcp-renew		\
	${j}network/dhcp-up			\
	/share/udhcpc/upstart.script"
RDEPENDS_${PN}-net-dhcp = "${PN}-net busybox"

FILES_${PN}-net-dhcp-eth0 = "${j}network/add-eth0-dhcp"
RDEPENDS_${PN}-net-dhcp-eth0 = "${PN}-net-dhcp"

FILES_${PN}-net-dhcp-eth1 = "${j}network/add-eth1-dhcp"
RDEPENDS_${PN}-net-dhcp-eth1 = "${PN}-net-dhcp"

FILES_${PN}-openntpd = " \
	/sbin/upstart-ntpd-up			\
	${j}services/openntpd			\
	${j}network/net-dhcp-ntp-renew"
RDEPENDS_${PN}-openntpd = "${PN}-net openntpd"

FILES_${PN}-dropbear = " \
	${j}init/dropbear		\
	${j}services/dropbear"
RDEPENDS_${PN}-dropbear   = "dropbear"
RPROVIDES_${PN}-dropbear  = "virtual/dropbear-init"

FILES_${PN}-rtc-sync = " \
	${j}init/rtc-sync	\
	${j}shutdown/rtc-sync	\
"

FILES_${PN}-tty-plain     = "${j}tty/plain"
RPROVIDES_${PN}-tty-plain = "virtual/upstart-tty"

FILES_${PN}-syslogd = "${j}services/syslogd"
RPROVIDES_${PN}-syslogd = "virtual/syslogd-init"
RDEPENDS_${PN}-syslogd = "busybox"

FILES_${PN}-klogd = "${j}services/klogd"
RPROVIDES_${PN}-klogd = "virtual/klogd-init"
RDEPENDS_${PN}-klogd = "busybox"

FILES_${PN}-sound = "${j}init/sound"
RPROVIDES_${PN}-sound = "virtual/sound-init"
RDEPENDS_${PN}-sound = "alsa-utils-alsactl"

FILES_${PN}-dbus = "${j}services/dbus-daemon ${j}init/dbus-uuidgen ${sysconfdir}/files.d/*-dbus.txt"
RPROVIDES_${PN}-dbus = "virtual/dbus-init"
RDEPENDS_${PN}-dbus = "dbus"

RPROVIDES_${PN}-empty = "virtual/tinylogin-init"

do_compile() {
}

do_install() {
	mkdir -p ${D}${sysconfdir}/init ${D}/sbin ${D}/share/udhcpc ${D}/lib/udev/rules.d ${D}${sysconfdir}/files.d
	cp -a jobs.d ${D}${sysconfdir}/init/

	install -p -m 0755 upstart-if-down upstart-if-up ${D}/sbin/
	install -p -m 0755 upstart-ntpd-up               ${D}/sbin/
	install -p -m 0755 upstart.script                ${D}/share/udhcpc/

	install -p -m 0644 [0-9][0-9]-*.rules            ${D}/lib/udev/rules.d/
	install -p -m 0644 [0-9][0-9]-*.txt              ${D}${sysconfdir}/files.d/

	find ${D} -type f -print0 | xargs -0r ${S}/fix-paths ${WORKDIR}/paths
}
