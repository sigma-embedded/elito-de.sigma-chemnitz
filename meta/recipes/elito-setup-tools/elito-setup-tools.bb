SECTION		= "base"
DESCRIPTION	= "ELiTo Base utilities"
PV		= "0.9"
PR		= "r1"
LICENSE		= "GPLv3"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=2c12447f794c304d9cd353f87a432c9e"

bindir		= "/bin"
sbindir		= "/sbin"

PACKAGE_ARCH	= "${MACHINE_ARCH}"
PACKAGES	=  "${PN}-dbg ${PN}"
RCONFLICTS_${PN} = "sysvinit"

DEPENDS        += "dietlibc-cross"

S               = "${WORKDIR}/elito-setup-${PV}"
SRC_URI		= " \
	${ELITO_MIRROR}/elito-setup-${PV}.tar.bz2 \
	file://mdev.dbg"

OVERRIDES .= "${@base_conditional('IMAGE_DEV_MANAGER','busybox-mdev',':mdev','',d)}"
OVERRIDES .= "${@base_contains('MACHINE_FEATURES','modules','',':nomodules',d)}"

FILES_${PN}	= "	\
	${sbindir}/*			\
	${bindir}/*			\
	/etc/tmpfiles.d			\
"

EXTRA_OEMAKE += "DIET=diet"

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

SRC_URI[md5sum] = "3688b523a90c4b02c62b238556c239a0"
SRC_URI[sha256sum] = "031c7aad6e82258eec1536dfa543d72106c015d039fd52d38782115a2a5de544"
