DESCRIPTION = "Miscellaneous files for systemd based system."
SECTION = "base"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"

SRCREV = "dbcc93880cf7da896b3b0bc7d1a03d9f7a864d2c"
_pv = "0.4"
PR = "r0"

PACKAGE_ARCH = "${MACHINE_ARCH}"

PV = "${_pv}+gitr${SRCPV}"
PKGV = "${_pv}+gitr${GITPKGV}"

inherit gitpkgv

SRC_URI		= " \
  ${ELITO_GIT_REPO}/pub/elito-systemd-conf.git \
  file://00-elito.conf \
  file://systemd.profile \
"

RRECOMMENDS_${PN} += "${PN}-firstboot"
RRECOMMENDS_${PN} += "${@base_contains('DISTRO_FEATURES','nfsroot','${PN}-nfs','', d)}"

FILES_${PN} = "\
  ${libdir}/tmpfiles.d/*.conf \
  ${sysconfdir}/profile.d/systemd.sh"

_d = "${datadir}/elito-systemd"
PACKAGES =+ "${PN}-nfs ${PN}-firstboot"

RDEPENDS_${PN}-nfs += "elito-setup-tools"
FILES_${PN}-nfs = "${_d}/nfs"

RDEPENDS_${PN}-firstboot += "elito-setup-tools"
FILES_${PN}-firstboot = "\
  ${systemd_unitdir}/system/firstboot* \
  ${systemd_unitdir}/system/*/firstboot* \
  /var/lib/firstboot"

do_install[dirs] = "${WORKDIR}"
do_install() {
    install -D -p -m 0644 00-elito.conf ${D}${libdir}/tmpfiles.d/00-elito.conf
    install -D -p -m 0644 systemd.profile ${D}${sysconfdir}/profile.d/systemd.sh

    install -d -m 0755 ${D}${_d} ${D}${systemd_unitdir}/system ${D}/var/lib/firstboot
    tar cf - -C git nfs --exclude=./.git --mode go-w,a+rX | tar xf - -C ${D}${_d}
    tar cf - -C git/all . --exclude=./.git --mode go-w,a+rX | tar xf - -C ${D}${systemd_unitdir}/system
}
