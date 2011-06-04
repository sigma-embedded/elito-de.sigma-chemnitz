## --*- python -*--
SECTION		= "base"
DESCRIPTION	= "upstart base setup"
LICENSE		= "GPLv3"

SRC_URI_append = " file://paths"

PACKAGES        = "${PN} ${PN}-base"

j                = "${sysconfdir}/init/"

ALLOW_EMPTY_${PN}       = "1"
RRECOMMENDS_${PN}      += " \
	${PN}-base		\
"

DEPENDS += "elito-setup-tools upstart"

FILES_${PN} = ""

# TODO: move udev rules and files.d into own package
FILES_${PN}-base = "				\
	${j}ctrl-alt-del.conf			\
	${j}halt.conf				\
	${j}init/boot.conf			\
	${j}init/dmesg.conf			\
	${j}init/mount-all.conf			\
	${j}init/mount-nonfs.conf		\
	${j}init/mount-tmpfs.conf		\
	${j}init/ldconfig.conf			\
	${j}shutdown/bind-mounts.conf		\
	${j}shutdown/shutdown.conf		\
	${j}wait/dev-urandom.conf               \
	${sysconfdir}/files.d/*-log.txt		\
"
FILES_${PN}-base_append_arm += "${j}init/cpu-align.conf"
RDEPENDS_${PN}-base         += "upstart elito-setup-tools"

PACKAGES_DYNAMIC = "${PN}-.*"

RCONFLICTS_${PN}-fsck-blockdev += "${PN}-fsck-dummy"
RCONFLICTS_${PN}-fsck-dummy    += "${PN}-fsck-blockdev"

RCONFLICTS_${PN}-mdev          += "${PN}-udev"
RCONFLICTS_${PN}-udev          += "${PN}-mdev"

RPROVIDES_${PN}-fsck-blockdev  += "virtual/${PN}-fsck"
RPROVIDES_${PN}-fsck-dummy     += "virtual/${PN}-fsck"

python populate_packages_prepend () {
        class P:
                def __init__(self, job_files, rdepends = None, extra_files = [],
                             rrecommends = None):
			self.job_files   = job_files
                        self.rdepends    = (rdepends,'')[rdepends == None]
                        self.extra_files = extra_files
                        self.rrecommends = rrecommends

	pkg_info = {
        'fsck-blockdev'	: P('init/fsck-all.conf', None, '/sbin/fsck-eval-result'),
        'fsck-dummy'	: P('init/fsck-dummy.conf', None),

        'udev'		: P(('init/udev-fill.conf',
                             'init/udev-modules.conf',
                             'services/udevd.conf'),
                            'udev',
                            '/lib/udev/rules.d/*-upstart.rules'),
        'mdev'		: P('init/mdev*.conf', 'busybox'),

        'net'		: P(('network/ifup.conf', 'network/ifdown.conf'),
                            None,
                            ('/sbin/upstart-if-down',
                             '/sbin/upstart-if-up')),
        'net-dhcp'      : P(('network/net-dhcp-deconfig.conf',
                             'network/net-dhcp-renew.conf',
                             'network/dhcp-up.conf'),
                            '${PN}-net busybox',
                            '/share/udhcpc/upstart.script'),

        'openntpd'	: P(('services/openntpd.conf',
                             'network/net-dhcp-ntp-renew.conf'),
                            '${PN}-net openntpd',
                            '/sbin/upstart-ntpd-up'),
        'dropbear'	: P(('init/dropbear.conf', 'services/dropbear.conf'),
                            'dropbear'),

        'rtc-sync'	: P(('init/rtc-sync.conf', 'shutdown/rtc-sync.conf')),

        'tty-plain'	: P('tty/plain.conf'),
        'tty-tinylogin'	: P('tty/tinylogin.conf'),

        'syslogd'	: P('services/syslogd.conf'),
        'klogd'		: P('services/klogd.conf'),

        'sound'		: P('init/sound.conf', 'alsa-utils-alsactl'),

        'dbus'		: P(('services/dbus-daemon.conf', 'init/dbus-uuidgen.conf'),
                            'dbus',
                            '${sysconfdir}/files.d/*-dbus.txt'),

        'hald'		: P(('services/hald.conf',),
                            'hal ${PN}-dbus',
                            '${sysconfdir}/files.d/*-hald.txt'),

        'acpid'		: P('services/acpid.conf', 'acpid'),
        'mcelog'	: P('services/mcelog.conf', 'mcelog'),

        'hald-acpid'	: P(('wait/hald-wait.conf',),
                            '${PN}-acpid ${PN}-hald'),
        }

        for (p,i) in pkg_info.items():
		elito_upstart_job(d, '${PN}-%s' % p,
                                  job_files   = i.job_files,
                                  rdepends    = i.rdepends + ' ${PN} (= ${PV}-${PR})',
                                  extra_files = i.extra_files,
                                  rrecommends = i.rrecommends)
}

do_configure_prepend() {
    sed -i -s 's!^include !#\0!g' Makefile
}

do_compile() {
}

do_install() {
	mkdir -p ${D}${sysconfdir} ${D}/sbin ${D}/share/udhcpc ${D}/lib/udev/rules.d ${D}${sysconfdir}/files.d
	cp -a jobs.d ${D}${sysconfdir}/init

        oe_runmake install-extras DESTDIR=${D}

	find ${D} -type f -print0 | xargs -0r ${S}/fix-paths ${WORKDIR}/paths
}
