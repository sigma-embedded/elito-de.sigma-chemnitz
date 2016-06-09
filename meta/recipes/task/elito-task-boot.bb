DESCRIPTION  = "Basic task to get a device booting to a prompt"
LICENSE      = "GPLv3"
PV	     = "1.1"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"

PACKAGE_ARCH = "${MACHINE_ARCH}"

do_distribute_sources() {
}

inherit packagegroup

#
# those ones can be set in machine config to supply packages needed to
# get machine booting
#
MACHINE_ESSENTIAL_EXTRA_RDEPENDS ?= ""
MACHINE_ESSENTIAL_EXTRA_RRECOMMENDS ?= ""

DISTRO_ESSENTIAL_EXTRA_RDEPENDS ?= ""
DISTRO_ESSENTIAL_EXTRA_RRECOMMENDS ?= ""

PROJECT_EXTRA_RDEPENDS ?= ""
PROJECT_EXTRA_RRECOMMENDS ?= ""

BASE_FILES_PROVIDER ?= "elito-base-files"
NETBASE_PROVIDER    ?= "elito-netbase"

# Make sure we build the kernel
DEPENDS = "virtual/kernel"

#
# minimal set of packages - needed to boot
#
RDEPENDS_${PN} += "\
    ${BASE_FILES_PROVIDER} \
    base-passwd \
    busybox \
    ${@bb.utils.contains('MACHINE_FEATURES', 'ubifs',   'mtd-utils', '', d)} \
    ${@bb.utils.contains('PROJECT_FEATURES', 'modules', 'module-init-tools',  '', d)} \
    update-alternatives \
    ${NETBASE_PROVIDER}	\
    ${MACHINE_ESSENTIAL_EXTRA_RDEPENDS} \
    ${DISTRO_ESSENTIAL_EXTRA_RDEPENDS}	\
    "

RRECOMMENDS_${PN} += "\
    ${MACHINE_ESSENTIAL_EXTRA_RRECOMMENDS}	\
    ${DISTRO_ESSENTIAL_EXTRA_RRECOMMENDS}	\
    "
