DESCRIPTION	= "Tools for managing memory technology devices."
SECTION		= "base"
HOMEPAGE	= "http://www.linux-mtd.infradead.org/"
LICENSE		= "GPLv2"
PV		= "1.3.1+gitr${SRCPV}"
INC_PR		= "r1"

DEFAULT_PREFERENCE = "99"

SRCREV  = "${AUTOREV}"
SRC_URI = "\
        git://git.infradead.org/mtd-utils.git;protocol=git	\
	file://0001-fixed-integer-underflow-in-jffs2_rtime_compress.patch	\
"
S = "${WORKDIR}/git"

EXTRA_OEMAKE  = "'CC=${CC}' 'CFLAGS=${CFLAGS} -I${S}/include -DWITHOUT_XATTR'"
PARALLEL_MAKE = ""
