SRCREV = "ab4672edbd29e170d260cddd5d048649791aa7e6"
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
