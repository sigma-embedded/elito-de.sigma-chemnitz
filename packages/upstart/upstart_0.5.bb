PV = 0.5.0
PR = "r8"


## dynamic libnih0 support is broken; resulting upstart dies with
##   initctl:list.c:141: Assertion failed in nih_list_add: list != NULL
##   Aborted
##   initctl:list.c:141: Assertion failed in nih_list_add: list != NULL
#OVERRIDES .= ':dyn'

require upstart.inc


SRC_URI = "	\
	http://upstart.ubuntu.com/download/0.5/upstart-${PV}.tar.gz				\
	file:///srv/elito/toolchain/devel/elito-upstart/upstart-0.5-oomadj.patch;patch=1	\
	file://upstart-0.5.0-dynlink								\
"

xtra          =
xtra_dyn      = "${PN}-lib ${PN}-tools"
PACKAGES     += "${PN}-sysv-tools ${PN}-compat ${xtra}"


FILES_${PN}-tools	 = "${bindir}/*"
FILES_${PN}-lib		 = "${libdir}/*.so.*"
LEAD_SONAME		 = "libnih.so"

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
