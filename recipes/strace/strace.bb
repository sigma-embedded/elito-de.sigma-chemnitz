DESCRIPTION = "strace is a system call tracing tool."
SECTION = "console/utils"
LICENSE = "GPL"
PV      = "4.5.19"
PR	= "r1"

SRC_URI = "${SOURCEFORGE_MIRROR}/strace/strace-${PV}.tar.bz2;name=tarball \
          "

SRC_URI[tarball.md5sum] = "2415e435d61e40315a298c80aced0cda"
SRC_URI[tarball.sha256sum] = "8997ce919e971b0ec45cd7006c6e1f9c7c0bce68ab59e3a629e1ddeda5013d08"

inherit autotools

do_install_append() {
	rm -f ${D}${bindir}/strace-graph
}
