require upstart.inc

PACKAGES     += "${PN}-sysv-tools ${PN}-compat"
PACKAGES     =+ "${PN}-samples"

FILES_${PN}-samples = "	\
	${sysconfdir}/init/*"

FILES_${PN} = "	\
	${sysconfdir}/dbus-*		\
	${sysconfdir}/init		\
	${base_sbindir}/init.upstart	\
	${base_sbindir}/initctl		\
"

FILES_${PN}-sysv-tools = " \
	${base_sbindir}/halt		\
	${base_sbindir}/reboot		\
	${base_sbindir}/poweroff		\
	${base_sbindir}/runlevel		\
	${base_sbindir}/shutdown		\
	${base_sbindir}/telinit		\
"
RDEPENDS_${PN}-sysv-tools = "${PN}"

FILES_${PN}-compat = " \
	${base_sbindir}/reload		\
	${base_sbindir}/restart		\
	${base_sbindir}/start		\
	${base_sbindir}/status		\
	${base_sbindir}/stop		\
"
RDEPENDS_${PN}-compat = "${PN}"
