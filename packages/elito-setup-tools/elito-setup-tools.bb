SECTION     	 = "base"
DESCRIPTION 	 = "ELiTo Base utilities"
LICENSE     	 = "GPLv3"
PV		 = "0.8.3"
PR          	 = "r0"

bindir      	 = "/bin"
sbindir     	 = "/sbin"

PACKAGE_ARCH	 = "${MACHINE_ARCH}"
PACKAGES	 =  "${PN}-dbg ${PN}"

SRC_URI     	 = "${ELITO_MIRROR}/elito-setup-${PV}.tar.bz2"
S                = "${WORKDIR}/elito-setup-${PV}"

RCONFLICTS_${PN} = "sysvinit"

FILES_${PN} 	 = "	\
	${sbindir}/*			\
	${bindir}/*			\
	/etc/files.d			\
"

FILES_${PN}-dbg	 = "	\
	${sbindir}/.debug		\
	${bindir}/.debug		\
"

do_compile() {
	oe_runmake
}

do_install() {
	oe_runmake DESTDIR=${D} install
	${@base_contains('MACHINE_FEATURES','modules',':','rm -f ${D}${sbindir}/elito-load-modules',d)}
}

pkg_postinst_${PN}() {
	update-alternatives --install /sbin/init init /sbin/init.wrapper 90
}

pkg_postrm_${PN}() {
	update-alternatives --remove init /sbin/init.wrapper
}
