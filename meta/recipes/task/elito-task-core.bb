DESCRIPTION  = "Basic task to get a device booting with core functionality"
LICENSE      = "GPLv3"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"

PACKAGE_ARCH = "${MACHINE_ARCH}"

PV = "1.1"

do_distribute_sources() {
}

inherit packagegroup

#
# udev, devfsd, busybox-mdev or none
#
IMAGE_DEV_MANAGER ?= "udev"

OVERRIDES .= "${@bb.utils.contains('IMAGE_DEV_MANAGER', 'udev', ':udev', '', d)}"
OVERRIDES .= "${@bb.utils.contains('IMAGE_DEV_MANAGER', 'busybox-mdev', ':mdev', '', d)}"

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
    ${@bb.utils.contains('MACHINE_FEATURES', 'ubifs', 'mtd-utils', '', d)} \
    ${@bb.utils.contains('MACHINE_FEATURES', 'keyboard', 'keymaps', '', d)} \
    ${IMAGE_INITSCRIPTS}		\
    ${VIRTUAL-RUNTIME_init_manager}	\
    ${PROJECT_EXTRA_RDEPENDS}		\
    ${_DEV_MANAGER_DEPS}		\
    "

RRECOMMENDS_${PN} += "\
    ${@bb.utils.contains('DISTRO_FEATURES','ipv6','kernel-module-ipv6','', d)} \
    ${PROJECT_EXTRA_RRECOMMENDS}	\
    "

do_populate_sysroot() {
}
addtask do_populate_sysroot
