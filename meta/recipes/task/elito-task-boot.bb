DESCRIPTION  = "Basic task to get a device booting to a prompt"
LICENSE      = "GPLv3"
PR           = "r12.${PROJECT_FILE_DATE}"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=2c12447f794c304d9cd353f87a432c9e"

do_distribute_sources() {
}

inherit task


# !! DO NOT MOVE IT TO TOP !!
# Else, the task class sets PACKAGE_ARCH to 'all' which will override
# value here.
PACKAGE_ARCH = "${MACHINE_ARCH}"

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
    ${@base_contains("MACHINE_FEATURES", "ubifs",   "mtd-utils-ubi-core", "", d)} \
    ${@base_contains("MACHINE_FEATURES", "modules", "module-init-tools",  "", d)} \
    update-alternatives \
    ${NETBASE_PROVIDER}	\
    ${MACHINE_ESSENTIAL_EXTRA_RDEPENDS} \
    ${DISTRO_ESSENTIAL_EXTRA_RDEPENDS}	\
    "

RRECOMMENDS_${PN} += "\
    ${MACHINE_ESSENTIAL_EXTRA_RRECOMMENDS}	\
    ${DISTRO_ESSENTIAL_EXTRA_RRECOMMENDS}	\
    "
