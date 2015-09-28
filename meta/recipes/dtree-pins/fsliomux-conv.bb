SUMMARY = "Converter for FreeScale IOMUx Tool files"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"

_pv     = "0.1.0"

PV   = "${_pv}+gitr${SRCPV}"
PKGV = "${_pv}+gitr${GITPKGV}"

SRCREV = "fb57e8e51371ba35324b8eb2ff41e370f255685d"
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
