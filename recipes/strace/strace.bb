DESCRIPTION = "strace is a system call tracing tool."
SECTION = "console/utils"
LICENSE = "GPL"
PV      = "4.5.18"
PR	= "r1"

SRC_URI = "${SOURCEFORGE_MIRROR}/strace/strace-${PV}.tar.bz2 \
	file://arm-2.6.patch;patch=p1	\
          "

inherit autotools

do_install_append() {
	rm -f ${D}${bindir}/strace-graph
}
