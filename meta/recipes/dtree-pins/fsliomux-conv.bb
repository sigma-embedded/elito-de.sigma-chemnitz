SUMMARY = "Converter for FreeScale IOMUx Tool files"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"

_pv     = "0.1.1"

PV   = "${_pv}+gitr${SRCPV}"
PKGV = "${_pv}+gitr${GITPKGV}"

SRCREV = "4f77af72abbaafe591d7b91d2ee62b6e1ac3e23c"
SRC_URI = "git://github.com/sigma-embedded/fsliomux-conv.git;protocol=https"

inherit gitpkgv

DEPENDS += "libxslt"
RDEPENDS_${PN} += "libxslt bash"
BBCLASSEXTEND = "native"

pkglibdir = "${libdir}/${BPN}"

S = "${WORKDIR}/git"

do_compile() {
	oe_runmake
}

do_install() {
	oe_runmake install DESTDIR=${D}
}
