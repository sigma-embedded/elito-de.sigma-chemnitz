## --*- python -*--
SECTION		= "base"
DESCRIPTION	= "upstart base setup"
LICENSE		= "GPLv3"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"

SRC_URI_append = " file://paths"

PACKAGES        = "${PN} ${PN}-base"

J                = "${sysconfdir}/init/"

ALLOW_EMPTY_${PN}       = "1"
RRECOMMENDS_${PN}      += " \
	${PN}-base		\
"

DEPENDS += "elito-setup-tools upstart"

FILES_${PN} = ""

# TODO: move udev rules and tmpfiles.d into own package
FILES_${PN}-base = "				\
	${J}ctrl-alt-del.conf			\
	${J}halt.conf				\
	${J}init/boot.conf			\
	${J}init/dmesg.conf			\
	${J}init/mount-all.conf			\
	${J}init/mount-nonfs.conf		\
	${J}init/mount-tmpfs.conf		\
	${J}init/ldconfig.conf			\
	${J}shutdown/bind-mounts.conf		\
	${J}shutdown/shutdown.conf		\
	${J}wait/dev-urandom.conf               \
	${sysconfdir}/tmpfiles.d/*-log.conf	\
"
FILES_${PN}-base_append_arm += "${J}init/cpu-align.conf"
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
                            '${sysconfdir}/tmpfiles.d/*-dbus.conf'),

        'hald'		: P(('services/hald.conf',),
                            'hal ${PN}-dbus',
                            '${sysconfdir}/tmpfiles.d/*-hald.conf'),

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
	mkdir -p ${D}${sysconfdir} ${D}/sbin ${D}/share/udhcpc ${D}/lib/udev/rules.d ${D}${sysconfdir}/tmpfiles.d
	cp -a jobs.d ${D}${sysconfdir}/init

        oe_runmake install-extras DESTDIR=${D}

	find ${D} -type f -print0 | xargs -0r ${S}/fix-paths ${WORKDIR}/paths
}
