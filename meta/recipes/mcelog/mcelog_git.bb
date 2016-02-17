HOMEPAGE = "http://www.kernel.org/pub/linux/utils/cpu/mce"
LICENSE = "GPL"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"

PV   = "1.0+git${SRCPV}"
PKGV = "1.0+git${GITPKGV}"

S    = "${WORKDIR}/git"

SRCREV   = "cbd4da48d16f5a781ee4f19d84b54c3875487942"
SRC_URI  = "git://git.kernel.org/pub/scm/utils/cpu/mce/mcelog.git;protocol=git"

inherit gitpkgv

do_compile() {
    oe_runmake -e
}

do_install() {
    oe_runmake -e install prefix=${D}/usr etcprefix=${D}
}
