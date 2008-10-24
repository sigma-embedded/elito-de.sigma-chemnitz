SECTION     	 = "base"
DESCRIPTION 	 = "ELiTo Base utilities"
LICENSE     	 = "GPLv3"
PR          	 = "r2"

bindir      	 = "/bin"
sbindir     	 = "/sbin"

base_uri    	 = "file:///srv/elito/toolchain/devel/elito-setup/"

PACKAGE_ARCH	 = "${MACHINE_ARCH}"
PACKAGES	 =  "${PN}-dbg ${PN}"

SRC_URI     	 = "		\
	${base_uri}GNUmakefile			\
	${base_uri}elito-genfiles.c		\
	${base_uri}elito-load-modules		\
	${base_uri}elito-wait-for-file.c	\
	${base_uri}init.wrapper.c		\
	${base_uri}redir-outerr.c		\
	${base_uri}sysctl.minit.c		\
	${base_uri}00-varfs.txt			\
"

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
	oe_runmake -f ${WORKDIR}/GNUmakefile VPATH=${WORKDIR}
}

do_install() {
	oe_runmake -f ${WORKDIR}/GNUmakefile VPATH=${WORKDIR} DESTDIR=${D} install
	${@base_contains('MACHINE_FEATURES','modules',':','rm -f ${D}${sbindir}/elito-load-modules',d)}
}

pkg_postinst_${PN}() {
	update-alternatives --install /sbin/init init /sbin/init.wrapper 90
}

pkg_postrm_${PN}() {
	update-alternatives --remove init /sbin/init.wrapper
}
