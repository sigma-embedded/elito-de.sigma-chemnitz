SECTION = "base"
DESCRIPTION = "ELiTo Base utilities"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"
PACKAGE_ARCH = "${MACHINE_ARCH}"

_pv = "0.11.4"
SRCREV = "5126cde5990e8b299abd6d8fffd4686f063fc1e7"

PV = "${_pv}+gitr${SRCPV}"
PKGV = "${_pv}+gitr${GITPKGV}"

SRC_URI = "${ELITO_PUBLIC_GIT_REPO}/elito-setup.git;protocol=git"
S = "${WORKDIR}/git"

inherit gitpkgv update-alternatives

PACKAGES =+ "${PN}-extra"
RCONFLICTS_${PN} = "sysvinit"
FILES_${PN} = " \
  ${base_sbindir}/* \
  ${base_bindir}/* \
"

FILES_${PN}-extra = "\
  ${bindir}/* \
"

OVERRIDES[vardeps] += "VIRTUAL-RUNTIME_init_manager"
OVERRIDES .= "${@oe.utils.conditional('VIRTUAL-RUNTIME_init_manager','systemd',':systemd','',d)}"
OVERRIDES .= "${@bb.utils.contains('DISTRO_FEATURES','initrd',':dietlibc','',d)}"

DEPENDS_append_dietlibc += "dietlibc-cross"
EXTRA_OEMAKE = "\
  bindir='${base_bindir}' \
  sbindir='${base_sbindir}' \
"
EXTRA_OEMAKE_append_dietlibc += "DIET=diet"

CPPFLAGS_append_systemd = '\
  -DENABLE_SYSTEMD=1 \
  -DSYSTEMD_TEMPLATE_DIR='"${datadir}/elito-systemd"' \
'

do_compile() {
    oe_runmake -e
}

do_install() {
    oe_runmake -e DESTDIR=${D} install

    install -d -m 0755 ${D}${bindir}
    mv ${D}${base_bindir}/elito-mmc-boot ${D}${bindir}/
}

sysroot_extraprogs() {
    install -D -p -m 0755 ${D}${bindir}/elito-mmc-boot ${SYSROOT_DESTDIR}${bindir}/elito-mmc-boot
}

SYSROOT_PREPROCESS_FUNCS += "sysroot_extraprogs"

ALTERNATIVE_${PN} = "init"

ALTERNATIVE_LINK_NAME[init] = "${base_sbindir}/init"
ALTERNATIVE_TARGET[init]    = "${base_sbindir}/init.wrapper"
ALTERNATIVE_PRIORITY[init]  = "900"
