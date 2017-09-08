DESCRIPTION	= "Miscellaneous files for the base system."
SECTION		= "base"
PRIORITY	= "required"
PV		= "1.1"
LICENSE		= "GPLv3"
PACKAGE_ARCH	= "${MACHINE_ARCH}"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"

FILESDIR	= "${OECORE_TOPDIR}/meta/recipes-core/base-files/base-files"

SRC_URI = "	\
	file://fstab			\
	file://hosts			\
	file://sysctl.conf		\
        file://device-order.conf	\
	file://qt.env			\
	file://var-cache.automount	\
"

RELEASE_FILES_PROVIDER ?= "elito-release"
RRECOMMENDS_${PN} = "${RELEASE_FILES_PROVIDER}"

FSTAB_ROOTFS_OPT ?= "defaults"
TMPFS_SIZE       ?= "8m"
TMP_SIZE	 ?= "${TMPFS_SIZE}"
PTS_GID          ?= "5"

B := "${S}"
S  = "${WORKDIR}"

do_install() {
	i() {
            test -s "$1" || return 0
	    install -D -p -m 0644 "$1" "${D}$2"
	}

	set -x
	cd ${S}
	mkdir -p ${D}${sysconfdir}

	for i in hosts; do
		install -D -p -m 0644 $i ${D}${sysconfdir}/$i
	done

	if ${@bb.utils.contains("PROJECT_FEATURES", "select-touch", "false", "test -s qt.env", d)}; then
		install -D -m 0644 qt.env ${D}${sysconfdir}/default/qt.env
	fi

	for i in resolv.conf.static ntpd.conf.static; do
		touch ${D}${sysconfdir}/$i
	done

	if ${@bb.utils.contains("DISTRO_FEATURES", "systemd", "true", "false", d)}; then
		install -D -p -m 0644 var-cache.automount ${D}${systemd_system_unitdir}/var-cache.automount
	fi

	ln -s ../run/resolv.conf ${D}/etc/resolv.conf
	ln -s ../run/ntpd.conf ${D}/etc/ntpd.conf

        i device-order.conf ${sysconfdir}/modprobe.d/10-device-order.conf
        i sysctl.conf       ${sysconfdir}/sysctl.d/10-elito.conf

	c1='-e /[[:space:]]debugfs[[:space:]]/d'
	c2='-e /[[:space:]]selinuxfs[[:space:]]/d'
	c3='-e /[[:space:]]unionfs[[:space:]]/d'
	c4='-e /[[:space:]]/boot[[:space:]]\+tmpfs[[:space:]]/d'
	c5='-e /x-systemd\./d'

	${@bb.utils.contains("PROJECT_FEATURES","no-kdebug",":","c1=",d)}
	${@bb.utils.contains("PROJECT_FEATURES","selinux","c2=",':',d)}
	${@bb.utils.contains("PROJECT_FEATURES","unionfs","c3=",':',d)}
	${@bb.utils.contains("PROJECT_FEATURES","hasboot",":","c4=",d)}
	${@bb.utils.contains("DISTRO_FEATURES","systemd","c5=",":",d)}

	sed $c0 $c1 $c2 $c3 $c4 $c5	\
		-e 's!@TMPFS_SIZE@!${TMPFS_SIZE}!g'	\
		-e 's!@TMP_SIZE@!${TMP_SIZE}!g'	\
		-e 's!@PTS_GID@!${PTS_GID}!g'		\
		-e 's!@ROOTFSOPT@!${FSTAB_ROOTFS_OPT}!g' \
		fstab > ${D}${sysconfdir}/fstab
}

PACKAGES        = "${PN}"
RDEPENDS_${PN}  = "elito-filesystem base-files"
CONFFILES_${PN} = "${sysconfdir}/fstab ${sysconfdir}/hosts \
	${sysconfdir}/nsswitch.conf \
	${sysconfdir}/resolv.conf.static ${sysconfdir}/ntpd.conf.static"

FILES_${PN}     = "\
  ${sysconfdir}/* \
  ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '${systemd_system_unitdir}/*', '', d)} \
"
