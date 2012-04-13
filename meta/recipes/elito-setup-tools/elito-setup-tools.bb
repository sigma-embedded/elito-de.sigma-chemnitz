SECTION = "base"
DESCRIPTION = "ELiTo Base utilities"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"
PACKAGE_ARCH = "${MACHINE_ARCH}"

_pv = "0.10"
PR = "r2"
SRCREV = "cfbfc73561044cef6a73ac1f9a92aeb90c7986c8"

PV = "${_pv}+gitr${SRCPV}"
PKGV = "${_pv}+gitr${GITPKGV}"

SRC_URI = "${ELITO_GIT_REPO}/pub/elito-setup.git;protocol=git"
S = "${WORKDIR}/git"

inherit gitpkgv update-alternatives

bindir = "/bin"
sbindir = "/sbin"

RCONFLICTS_${PN} = "sysvinit"
FILES_${PN} = " \
  ${sbindir}/* \
  ${bindir}/* \
"

OVERRIDES .= "${@base_conditional('IMAGE_INIT_MANAGER','systemd',':systemd','',d)}"
OVERRIDES .= "${@base_contains('DISTRO_FEATURES','dietlibc',':dietlibc','',d)}"

DEPENDS_append_dietlibc += "dietlibc-cross"
EXTRA_OEMAKE_append_dietlibc += "DIET=diet"

CPPFLAGS_append_systemd = '\
  -DENABLE_SYSTEMD=1 \
  -DSYSTEMD_TEMPLATE_DIR='"${datadir}/elito-systemd"' \
'

do_compile() {
    oe_runmake
}

do_install() {
    oe_runmake DESTDIR=${D} install
}

ALTERNATIVE_NAME = "init"
ALTERNATIVE_PATH = "${sbindir}/init.wrapper"
ALTERNATIVE_LINK = "${sbindir}/init"
ALTERNATIVE_PRIORITY = "900"
