SECTION		= "base"
DESCRIPTION	= "ELiTo Base utilities"
LICENSE		= "GPLv3"
PV		= "0.8.8"
PR		= "r0"

bindir		= "/bin"
sbindir		= "/sbin"

PACKAGE_ARCH	= "${MACHINE_ARCH}"
PACKAGES	=  "${PN}-dbg ${PN}"
RCONFLICTS_${PN} = "sysvinit"

SRC_URI		= " \
	${ELITO_MIRROR}/elito-setup-${PV}.tar.bz2 \
	file://mdev.dbg"

S               = "${WORKDIR}/elito-setup-${PV}"

OVERRIDES	.= "${@base_conditional('IMAGE_DEV_MANAGER','busybox-mdev',':mdev','',d)}"
OVERRIDES	.= "${@base_contains('MACHINE_FEATURES','modules','',':nomodules',d)}"


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
