DESCRIPTION = "ELiTo testsuite"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"

PV = "0.0.1+gitr${SRCPV}"
PR = "r0"

inherit gitpkgv

SRCREV = "bf63336cf699aec72e445248679983cc0ae5969c"
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
