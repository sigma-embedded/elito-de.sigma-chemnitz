DESCRIPTION	= "Miscellaneous files for the base system."
SECTION		= "base"
PRIORITY	= "required"
PR		= "r3"
LICENSE		= "GPLv3"
PACKAGE_ARCH	= "${MACHINE_ARCH}"

FILESDIR	= "${OEDEV_TOPDIR}/packages/base-files/base-files"

SRC_URI = "	\
	file://fstab			\
	file://hosts			\
	file://nsswitch.conf		\
	file://sysctl.conf		\
	file://licenses/*		\
"

RPROVIDES_${PN}   = "virtual/base-files"
RRECOMMENDS_${PN} = "virtual/release-files"

TMPFS_SIZE       ?= 8m
PTS_GID          ?= 5

do_install() {
	set -x
	cd ${WORKDIR}
	mkdir -p ${D}${sysconfdir} ${D}${datadir}/doc/

	for i in hosts nsswitch.conf sysctl.conf; do
		install -D -p -m0644 $i ${D}${sysconfdir}/$i
	done

	for i in resolv.conf ntpd.conf; do
		touch ${D}${sysconfdir}/$i
	done

	echo ${MACHINE} > ${D}${sysconfdir}/hostname

	c0='-e /[[:space:]]usbfs[[:space:]]/d'
	c1='-e /[[:space:]]debugfs[[:space:]]/d'
	c2='-e /[[:space:]]selinuxfs[[:space:]]/d'

	${@base_contains("MACHINE_FEATURES","usb","c0=",':',d)}
	${@base_contains("MACHINE_FEATURES","kdebug","c1=",':',d)}
	${@base_contains("MACHINE_FEATURES","selinux","c2=",':',d)}

	sed $c0 $c1 $c2	\
		-e 's!@TMPFS_SIZE@!${TMPFS_SIZE}!g'	\
		-e 's!@PTS_GID@!${PTS_GID}!g'		\
		fstab > ${D}${sysconfdir}/fstab

	cp -a licenses ${D}${datadir}/doc/

	ln -s /proc/mounts ${D}${sysconfdir}/mtab

}

PACKAGES        = "${PN}"
RDEPENDS_${PN}  = "elito-filesystem"
CONFFILES_${PN} = "${sysconfdir}/fstab ${sysconfdir}/hosts \
	${sysconfdir}/nsswitch.conf ${sysconfdir}/sysctl.conf	\
	${sysconfdir}/resolv.conf   ${sysconfdir}/ntpd.conf	\
	${sysconfdir}/hostname"

FILES_${PN}     = "${sysconfdir}/* ${datadir}/doc/licenses"
