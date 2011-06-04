HOMEPAGE = "http://www.kernel.org/pub/linux/utils/cpu/mce"
LICENSE = "GPL"

PV   = "1.0"
PR   = "r0"

PRE  = "pre3"
PKGV = "${PV}~${PRE}"

S    = "${WORKDIR}/${PN}-${PV}${PRE}"

SRC_URI  = "http://www.kernel.org/pub/linux/utils/cpu/mce/mcelog-${PV}${PRE}.tar.bz2;name=tarball"

do_compile() {
    oe_runmake
}

do_install() {
    oe_runmake install prefix=${D}/usr etcprefix=${D}
}

SRC_URI[tarball.md5sum] = "dbdf6507eb34ebc4ce7c583ef6c568c5"
SRC_URI[tarball.sha256sum] = "c30e903da526899bf6ea24278d74bdcfadc68e0500d246de714582591aded323"
