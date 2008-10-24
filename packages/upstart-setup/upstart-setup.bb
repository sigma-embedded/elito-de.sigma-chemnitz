SECTION  	 = "base"
DESCRIPTION	 = "upstart base setup"
LICENSE    	 = "GPLv3"
PV		 = "0.2.3"
PR         	 = "r0"
PACKAGE_ARCH	 =  "all"

SRC_URI    	 = "		\
#	git://localhost/elito-upstart-setup;protocol=git		\
	file:///srv/elito/toolchain/devel/elito-upstart-setup/${PN}-${PV}.tar.bz2	\
"
SRCREV		 = "${AUTOREV}"
#S     		 = "${WORKDIR}/git"

PACKAGES   	 = "${PN}-base ${PN}-udev ${PN}-net ${PN}-net-dhcp	\
	${PN}-openntpd ${PN}-dropbear ${PN}-tty-plain"

j		 = "${sysconfdir}/init/jobs.d/"

FILES_${PN}-base = "				\
	${j}ctrl-alt-del ${j}halt		\
	${j}init/boot ${j}init/dmesg ${j}init/mount-all ${j}init/mount-tmpfs	\
	${j}init/ldconfig							\
	${j}shutdown/bind-mounts ${j}shutdown/shutdown				\
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

FILES_${PN}-openntpd = " \
	/sbin/upstart-ntpd-up			\
	${j}services/openntpd			\
	${j}network/net-dhcp-ntp-renew"
RDEPENDS_${PN}-net-dhcp = "${PN}-net"

FILES_${PN}-dropbear = " \
	${j}init/dropbear		\
	${j}services/dropbear"
RDEPENDS_${PN}-dropbear = "dropbear"

FILES_${PN}-tty-plain     = "${j}tty/plain"
RPROVIDES_${PN}-tty-plain = "virtual/upstart-tty"

do_compile() {
}

do_install() {
	mkdir -p ${D}${sysconfdir}/init ${D}/sbin ${D}/share/udhcpc
	cp -a jobs.d ${D}${sysconfdir}/init/

	install -p -m0755 upstart-if-down upstart-if-up ${D}/sbin/
	install -p -m0755 upstart-ntpd-up               ${D}/sbin/
	install -p -m0755 upstart.script                ${D}/share/udhcpc/
}
