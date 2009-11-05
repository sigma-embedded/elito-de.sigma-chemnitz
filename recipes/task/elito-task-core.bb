DESCRIPTION  = "Basic task to get a device booting with core functionality"
LICENSE      = "GPLv3"
PR           = "r5.${PROJECT_FILE_DATE}"

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
IMAGE_DEV_MANAGER ?= "${@base_contains("MACHINE_FEATURES", "kernel26",  "udev","",d)} "

OVERRIDES .= "${@base_contains("IMAGE_DEV_MANAGER", "udev", ":udev", "", d)}"
OVERRIDES .= "${@base_contains("IMAGE_DEV_MANAGER", "busybox-mdev", ":mdev", "", d)}"

EXTRA_DEV_RULES ?=
EXTRA_DEV_RULES_append = " ${DEVFS_INIT_PROVIDER}"
EXTRA_DEV_RULES_append = " ${@base_contains('MACHINE_FEATURES','firmware','${FIRMWARE_LOADER_PROVIDER}','',d)}"
EXTRA_DEV_RULES_append_udev = " ${@base_contains("MACHINE_FEATURES","modules","udev-rules-modules","",d)}"

#
# sysvinit, upstart
#
IMAGE_INIT_MANAGER ?= "upstart"

#
# tinylogin, getty
#
IMAGE_LOGIN_MANAGER ?= "tinylogin"

PROJECT_EXTRA_RDEPENDS ?= ""
PROJECT_EXTRA_RRECOMMENDS ?= ""

# Make sure we build the kernel
DEPENDS = "elito-task-boot"

#
# minimal set of packages - needed to boot
#
RDEPENDS_${PN} += "\
    elito-task-boot			\
    ${@base_contains("MACHINE_FEATURES", "ubifs", "mtd-utils-ubi-tools", "", d)} \
    ${@base_contains("MACHINE_FEATURES", "keyboard", "keymaps", "", d)} \
    ${IMAGE_INITSCRIPTS}		\
    ${EXTRA_DEV_RULES}			\
    ${IMAGE_DEV_MANAGER}		\
    ${IMAGE_INIT_MANAGER}		\
    ${IMAGE_LOGIN_MANAGER}		\
    ${PROJECT_EXTRA_RDEPENDS}		\
    "

RRECOMMENDS_${PN} += "\
    ${PROJECT_EXTRA_RRECOMMENDS}	\
    "
