SRCREV = "bcd92892883b53ddd8c6dafd65d223cf09d78fc3"
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
