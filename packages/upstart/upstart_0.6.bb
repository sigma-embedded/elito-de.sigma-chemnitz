PV = 0.6.1
PR = "r1"


## dynamic libnih0 support is broken; resulting upstart dies with
##   initctl:list.c:141: Assertion failed in nih_list_add: list != NULL
##   Aborted
##   initctl:list.c:141: Assertion failed in nih_list_add: list != NULL
OVERRIDES .= ':dyn'

require upstart.inc


SRC_URI = "	\
	http://upstart.ubuntu.com/download/0.6/upstart-${PV}.tar.bz2				\
	file://upstart-0.5-oomadj.patch;patch=1							\
"

xtra          =
xtra_dyn      = "${PN}-lib ${PN}-tools"
PACKAGES     += "${PN}-sysv-tools ${PN}-compat ${xtra}"
PACKAGES     =+ "${PN}-samples"


FILES_${PN}-tools	 = "${bindir}/*"
FILES_${PN}-lib		 = "${libdir}/*.so.*"
LEAD_SONAME		 = "libnih.so"

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
	/sbin/start		\
	/sbin/status		\
	/sbin/stop		\
"
RDEPENDS_${PN}-compat = "${PN}"
