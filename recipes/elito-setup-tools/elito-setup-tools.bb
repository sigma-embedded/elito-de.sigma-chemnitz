SECTION		= "base"
DESCRIPTION	= "ELiTo Base utilities"
LICENSE		= "GPLv3"
PV		= "0.8.11"
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

SRC_URI[tarball.md5sum] = "63e2e5422de95c84fa3f2f844d30695f"
SRC_URI[tarball.sha256sum] = "138a7dfeda1044563d5687d9e47d2e68142c7fd0e4cd65afc74d545b5daf2c08"

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
