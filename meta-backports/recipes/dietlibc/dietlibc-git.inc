SRCREV = "19f8f5bb87e31b11b23d916a9afb5f751ab8df28"
_pv = "0.32"
PR = "r0"

inherit gitpkgv

PV = "${_pv}+gitr${SRCPV}"
PKGV = "${_pv}+gitr${GITPKGV}"

SRC_URI = " \
        git://github.com/ensc/dietlibc.git;protocol=git;branch=master \
        file://ccache.patch \
        file://runtests-X.sh \
"
S = "${WORKDIR}/git"
