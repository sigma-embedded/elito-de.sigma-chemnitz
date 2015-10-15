DESCRIPTION = "Miscellaneous files for systemd based system."
SECTION = "base"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"

SRCREV = "c747751e1e1e7cd9f8ea50b0452c18c97af80f61"
_pv = "0.4.11"

PACKAGE_ARCH = "${MACHINE_ARCH}"

PV = "${_pv}+gitr${SRCPV}"
PKGV = "${_pv}+gitr${GITPKGV}"

BOOTDEVICE = "eth0"

inherit gitpkgv

SRC_URI		= " \
  ${ELITO_GIT_REPO}/pub/elito-systemd-conf.git \
  file://05-elito.conf \
  file://05-elito-network.conf \
  file://05-elito-network_systemd.conf \
  file://systemd.profile \
  file://device-touchscreen.target \
"

S = "${WORKDIR}/git"

RRECOMMENDS_${PN} += "${PN}-firstboot"
RRECOMMENDS_${PN} += "${@base_contains('DISTRO_FEATURES','nfsroot','${PN}-nfs','', d)}"

FILES_${PN} = "\
  ${systemd_unitdir}/system/device-touchscreen.target \
  ${libdir}/tmpfiles.d/*.conf \
  ${sysconfdir}/profile.d/systemd.sh"

_d = "${datadir}/elito-systemd"
PACKAGES =+ "${PN}-nfs ${PN}-firstboot ${PN}-network"

RDEPENDS_${PN}-nfs += "elito-setup-tools"
FILES_${PN}-nfs = "${_d}/nfs"

RDEPENDS_${PN}-firstboot += "elito-setup-tools"
FILES_${PN}-firstboot = "\
  ${systemd_unitdir}/system/firstboot* \
  ${systemd_unitdir}/system/*/firstboot* \
  /var/lib/firstboot"

RDEPENDS_${PN}-network += "systemd-networkd"
FILES_${PN}-network = "\
  ${systemd_unitdir}/network/*.network \
"

do_configure() {
    sed -e 's!@prefix@!${prefix}!g' \
        -e 's!@bootdev@!${BOOTDEVICE}!g' \
        -i nfs/connman.service

    sed -e 's!@BOOTDEV@!${BOOTDEVICE}!g' \
        -i network/*.network
}

_TAR_OPTS = "--exclude=./.git --mode go-w,a+rX --owner root --group root"

copy_by_tar() {
    tar cf - -C $1 $2 ${_TAR_OPTS} | tar xf - -C ${D}$3
}

do_install[dirs] = "${WORKDIR}"
do_install() {
    f='05-elito-network_${ELITO_NETWORKD}.conf'
    test -e "$f" || f='05-elito-network.conf'
    install -D -p -m 0644 "$f" ${D}${libdir}/tmpfiles.d/05-elito-network.conf

    install -D -p -m 0644 05-elito.conf ${D}${libdir}/tmpfiles.d/05-elito.conf
    install -D -p -m 0644 systemd.profile ${D}${sysconfdir}/profile.d/systemd.sh

    install -D -p -m 0644 device-touchscreen.target \
        ${D}${systemd_unitdir}/system/device-touchscreen.target

    install -d -m 0755 \
	${D}${_d} ${D}${systemd_unitdir}/system \
	${D}${_d} ${D}${systemd_unitdir}/network \
	${D}/var/lib/firstboot

    opts=

    copy_by_tar "git"         "nfs" "${_d}"
    copy_by_tar "git/all"     "."   "${systemd_unitdir}/system"
    copy_by_tar "git/network" "."   "${systemd_unitdir}/network"
}
