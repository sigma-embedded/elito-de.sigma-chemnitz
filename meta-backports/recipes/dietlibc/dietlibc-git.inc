SRCREV = "ec78a5f301360f22e7a9726dbd9223123c7903ed"
_pv = "0.33"
PR = "r0"

inherit gitpkgv

PV = "${_pv}+gitr${SRCPV}"
PKGV = "${_pv}+gitr${GITPKGV}"

SRC_URI = " \
        git://github.com/ensc/dietlibc.git;protocol=git \
        file://ccache.patch \
"
S = "${WORKDIR}/git"
