DESCRIPTION = "Generates makefile in workspace directory"
HOMEPAGE = "http://elito.sigma-chemnitz.de"
PV = "0.4+${PROJECT_CONF_DATE}"
PR = "r2"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"

PACKAGES = ""
PACKAGE_ARCH = "${MACHINE_ARCH}"
PROVIDES = "elito-develcomp-native"

DEPENDS = "elito-makefile"
INHIBIT_DEFAULT_DEPS = "1"

###########

IPKGCONF_TARGET = "${WORKDIR}/ipkg-conf/opkg.conf"
IPKGCONF_SDK = "${WORKDIR}/ipkg-conf/opkg-sdk.conf"

do_setup_ipkg[depends] = "${@base_contains('MACHINE_FEATURES', 'nokernel', '', 'elito-image:do_rootfs',d)}"
do_setup_ipkg[dirs] = "${WORKDIR}/ipkg-conf"
do_setup_ipkg() {
        package_generate_ipkg_conf
}

SSTATETASKS += "do_setup_ipkg"
do_setup_ipkg[sstate-name] = "setup-ipk"
do_setup_ipkg[sstate-inputdirs] = "${WORKDIR}/ipkg-conf"
do_setup_ipkg[sstate-outputdirs] = "${DEPLOY_DIR_IPK}"

addtask setup_ipkg before do_package_write after do_package
