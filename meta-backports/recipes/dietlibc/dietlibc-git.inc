SRCREV = "1a376cf93e88c76e6b8b118c03625ac7945be031"
_pv = "0.33"

inherit gitpkgv

PV = "${_pv}+gitr${SRCPV}"
PKGV = "${_pv}+gitr${GITPKGV}"

SRC_URI = " \
        git://github.com/ensc/dietlibc.git;protocol=git \
        file://ccache.patch \
"
S = "${WORKDIR}/git"
