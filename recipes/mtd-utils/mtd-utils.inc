DESCRIPTION	= "Tools for managing memory technology devices."
SECTION		= "base"
HOMEPAGE	= "http://www.linux-mtd.infradead.org/"
LICENSE		= "GPLv2"
PV		= "1.2.0+gitr${SRCPV}"
INC_PR		= "r0"

DEFAULT_PREFERENCE = "99"

SRCREV  = "${AUTOREV}"
SRC_URI = "\
        git://git.infradead.org/mtd-utils.git;protocol=git"	\
"
S = "${WORKDIR}/git"

EXTRA_OEMAKE = "'CC=${CC}' 'CFLAGS=${CFLAGS} -I${S}/include -DWITHOUT_XATTR'"
