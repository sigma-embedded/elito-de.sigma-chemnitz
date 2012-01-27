SRCREV = "a81f77eb83672af5ebf4dffe5ba2b9e0d0e32124"
_pv = "0.32"
PR = "r0"

inherit gitpkgv

PV = "${_pv}+gitr${SRCPV}"
PKGV = "${_pv}+gitr${GITPKGV}"

SRC_URI = " \
        git://github.com/ensc/dietlibc.git;protocol=git \
        file://ccache.patch \
        file://runtests-X.sh \
"
S = "${WORKDIR}/git"
