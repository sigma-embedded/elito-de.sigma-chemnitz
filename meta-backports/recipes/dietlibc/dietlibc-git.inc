SRCREV = "cf50b2f65188129769737cdc670eeb54ab28fb23"
_pv = "0.32"
PR = "r0"

inherit gitpkgv

PV = "${_pv}+gitr${SRCPV}"
PKGV = "${_pv}+gitr${GITPKGV}"

SRC_URI = " \
        git://github.com/ensc/dietlibc.git;protocol=git \
        file://ccache.patch \
"
S = "${WORKDIR}/git"
