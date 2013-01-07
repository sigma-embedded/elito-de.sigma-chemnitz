DESCRIPTION = "ELiTo testsuite"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"


SRCREV = "3a702f9ca3b8b74669757cb87a9b354523524241"
_pv = "0.0.1"
PR = "r0"

PV = "${_pv}+gitr${SRCPV}"
PKGV = "${_pv}+gitr${GITPKGV}"

inherit gitpkgv

SRC_URI = "${ELITO_GIT_REPO}/pub/elito-testsuite.git;protocol=git"
S = "${WORKDIR}/git"

do_compile() {
	oe_runmake
}

do_install() {
	oe_runmake install DESTDIR='${D}'

        mv ${D}${bindir}/runtests ${D}${bindir}/elito-runtests
}

RDEPENDS_${PN} += "make"
FILES_${PN}-dbg += "${libexecdir}/*/.debug"

python populate_packages_prepend () {
    d.appendVar("RRECOMMENDS_${PN}", " virtual/elito-testsuite")
}
