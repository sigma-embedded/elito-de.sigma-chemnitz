SRCREV = "183e783981f53719a1696af29da3ae9c7ee70453"
_pv = "0.33"

inherit gitpkgv

PV = "${_pv}+gitr${SRCPV}"
PKGV = "${_pv}+gitr${GITPKGV}"

SRC_URI = " \
        git+https://github.com/ensc/dietlibc.git \
        file://ccache.patch \
"
S = "${WORKDIR}/git"
