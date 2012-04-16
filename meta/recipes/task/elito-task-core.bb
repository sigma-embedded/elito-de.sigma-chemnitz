DESCRIPTION  = "Basic task to get a device booting with core functionality"
LICENSE      = "GPLv3"
PR           = "r13.${PROJECT_FILE_DATE}"
LIC_FILES_CHKSUM = "file://${COREBASE}/LICENSE;md5=3f40d7994397109285ec7b81fdeb3b58"

do_distribute_sources() {
}

inherit task

# !! DO NOT MOVE IT TO TOP !!
# Else, the task class sets PACKAGE_ARCH to all which will override
# value here.
PACKAGE_ARCH = "${MACHINE_ARCH}"

#
# udev, devfsd, busybox-mdev or none
#
IMAGE_DEV_MANAGER ?= "${@base_contains('MACHINE_FEATURES', 'kernel26',  'udev','',d)} "

OVERRIDES .= "${@base_contains('IMAGE_DEV_MANAGER', 'udev', ':udev', '', d)}"
OVERRIDES .= "${@base_contains('IMAGE_DEV_MANAGER', 'busybox-mdev', ':mdev', '', d)}"

EXTRA_DEV_RULES ?= ""
EXTRA_DEV_RULES_append = " ${DEVFS_INIT_PROVIDER}"

#
# sysvinit, upstart
#
IMAGE_INIT_MANAGER ?= "upstart"

PROJECT_EXTRA_RDEPENDS ?= ""
PROJECT_EXTRA_RRECOMMENDS ?= ""

MACHINE_EXTRA_DEPENDS ?= ""

# Make sure we build the kernel
DEPENDS = "elito-task-boot ${MACHINE_EXTRA_DEPENDS}"

_DEV_MANAGER_DEPS_udev += "udev"
_DEV_MANAGER_DEPS_mdev += "busybox elito-mdev-conf"

RDEPENDS_${PN} =+ " \
    elito-filesystem	\
"

#
# minimal set of packages - needed to boot
#
RDEPENDS_${PN} += "\
    elito-task-boot			\
    ${@base_contains('MACHINE_FEATURES', 'ubifs', 'mtd-utils', '', d)} \
    ${@base_contains('MACHINE_FEATURES', 'keyboard', 'keymaps', '', d)} \
    ${IMAGE_INITSCRIPTS}		\
    ${EXTRA_DEV_RULES}			\
    ${IMAGE_INIT_MANAGER}		\
    ${PROJECT_EXTRA_RDEPENDS}		\
    ${_DEV_MANAGER_DEPS}		\
    "

RRECOMMENDS_${PN} += "\
    ${@base_contains('DISTRO_FEATURES','ipv6','kernel-module-ipv6','', d)} \
    ${@elito_base_switch(d, 'DISTRO_TYPE','debug','elito-testsuite','')} \
    ${PROJECT_EXTRA_RRECOMMENDS}	\
    "
