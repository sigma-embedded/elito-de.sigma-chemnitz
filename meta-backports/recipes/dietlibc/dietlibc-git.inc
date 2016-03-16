SRCREV = "87a59f2e6dc11d9f5b82c9d6eff09aae2daa1bbb"
_pv = "0.33"

inherit gitpkgv

PV = "${_pv}+gitr${SRCPV}"
PKGV = "${_pv}+gitr${GITPKGV}"

SRC_URI = " \
        git://github.com/ensc/dietlibc.git;protocol=git \
        file://ccache.patch \
        file://no-extern-def.patch \
"
S = "${WORKDIR}/git"
