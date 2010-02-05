PR = "r0"

require upstart.inc

PACKAGES     += "${PN}-sysv-tools ${PN}-compat ${xtra}"
PACKAGES     =+ "${PN}-samples"

FILES_${PN}-dev += "/lib/*.so"

FILES_${PN}-samples = "	\
	/etc/init/*"

FILES_${PN} = "	\
	/etc/dbus-*		\
	/etc/init		\
	/sbin/init.upstart	\
	/sbin/initctl		\
"

FILES_${PN}-sysv-tools = " \
	/sbin/halt		\
	/sbin/reboot		\
	/sbin/poweroff		\
	/sbin/runlevel		\
	/sbin/shutdown		\
	/sbin/telinit		\
"
RDEPENDS_${PN}-sysv-tools = "${PN}"

FILES_${PN}-compat = " \
	/sbin/reload		\
	/sbin/restart		\
	/sbin/start		\
	/sbin/status		\
	/sbin/stop		\
"
RDEPENDS_${PN}-compat = "${PN}"
