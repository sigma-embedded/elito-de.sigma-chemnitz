HOMEPAGE = "http://www.kernel.org/pub/linux/utils/cpu/mce"
LICENSE = "GPL"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"

PV   = "1.0+git${SRCPV}"
PKGV = "1.0+git${GITPKGV}"
PR   = "r0"

S    = "${WORKDIR}/git"

SRCREV   = "5dbfa53be2e737632bb5e58458131c415a23461c"
SRC_URI  = "git://git.kernel.org/pub/scm/utils/cpu/mce/mcelog.git;protocol=git"

inherit gitpkgv

do_compile() {
    oe_runmake
}

do_install() {
    oe_runmake install prefix=${D}/usr etcprefix=${D}
}
