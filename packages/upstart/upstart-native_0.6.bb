PV = 0.6.1
PR = "r0"


## dynamic libnih0 support is broken; resulting upstart dies with
##   initctl:list.c:141: Assertion failed in nih_list_add: list != NULL
##   Aborted
##   initctl:list.c:141: Assertion failed in nih_list_add: list != NULL
OVERRIDES .= ':dyn'

require upstart-native.inc


SRC_URI = "	\
	http://upstart.ubuntu.com/download/0.6/upstart-${PV}.tar.bz2				\
	file://upstart-0.5-oomadj.patch;patch=1							\
"

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
