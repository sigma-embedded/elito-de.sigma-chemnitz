SECTION		= "base"
DESCRIPTION	= "ELiTo Base utilities"
LICENSE		= "GPLv3"
PV		= "0.8.10"
PR		= "r0"

bindir		= "/bin"
sbindir		= "/sbin"

PACKAGE_ARCH	= "${MACHINE_ARCH}"
PACKAGES	=  "${PN}-dbg ${PN}"
RCONFLICTS_${PN} = "sysvinit"

S               = "${WORKDIR}/elito-setup-${PV}"
SRC_URI		= " \
	${ELITO_MIRROR}/elito-setup-${PV}.tar.bz2;name=tarball \
	file://mdev.dbg"

SRC_URI[tarball.md5sum]    = "7dd0ba7f011c8f795770af0a93ef9cd6"
SRC_URI[tarball.sha256sum] = "bed00807ee2ae62b0ef07d2fe62dff03fd78c3f510000c3259065262c7be26f5"


OVERRIDES .= "${@base_conditional('IMAGE_DEV_MANAGER','busybox-mdev',':mdev','',d)}"
OVERRIDES .= "${@base_contains('MACHINE_FEATURES','modules','',':nomodules',d)}"

FILES_${PN}	= "	\
	${sbindir}/*			\
	${bindir}/*			\
	/etc/files.d			\
"

do_compile() {
	oe_runmake
}


do_install() {
    oe_runmake DESTDIR=${D} install
}

do_install_append_mdev() {
    install -p -m 0755 ../mdev.dbg ${D}/sbin/mdev.dbg
}

do_install_append_nomodules() {
    rm -f ${D}${sbindir}/elito-load-modules
}


pkg_postinst_${PN}() {
	update-alternatives --install /sbin/init init /sbin/init.wrapper 90
}

pkg_postrm_${PN}() {
	update-alternatives --remove init /sbin/init.wrapper
}
