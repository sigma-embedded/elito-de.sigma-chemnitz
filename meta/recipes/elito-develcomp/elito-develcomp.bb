DESCRIPTION = "Generates makefile in workspace directory"
HOMEPAGE = "http://elito.sigma-chemnitz.de"
PV = "0.6"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"

PACKAGES = ""
PACKAGE_ARCH = "${MACHINE_ARCH}"
PROVIDES = "elito-develcomp-native"

inherit elito-nfsroot

_machdeps = ""
_machdeps_mx6 = "elito-devicetree-tools-cross-${TARGET_ARCH}"

DEPENDS = "elito-makefile ${_machdeps}"
INHIBIT_DEFAULT_DEPS = "1"

###########

IPKGCONF_TARGET = "${WORKDIR}/ipkg-conf/opkg.conf"
IPKGCONF_SDK = "${WORKDIR}/ipkg-conf/opkg-sdk.conf"

do_build[depends] += "elito-makefile:do_prepare_all_sysroots"
do_build[depends] += "kernel-makefile:do_setup_makefile"

do_setup_ipkg[dirs] = "${WORKDIR}/ipkg-conf"
do_setup_ipkg[nostamp] = "1"
python do_setup_ipkg() {
    from oe.package_manager import OpkgPM

    OpkgPM(d,
           d.getVar('IMAGE_ROOTFS', True),
           d.getVar("IPKGCONF_TARGET", True),
           d.getVar("ALL_MULTILIB_PACKAGE_ARCHS", True))
}

SSTATETASKS += "do_setup_ipkg"
do_setup_ipkg[sstate-name] = "setup-ipk"
do_setup_ipkg[sstate-inputdirs] = "${WORKDIR}/ipkg-conf"
do_setup_ipkg[sstate-outputdirs] = "${DEPLOY_DIR_IPK}"

addtask do_setup_ipkg
