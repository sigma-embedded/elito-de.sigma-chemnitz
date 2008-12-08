DESCRIPTION = "Basic task to get a device booting with core functionality"
PR = "r0.${PROJECT_FILE_DATE}"

inherit task

# packages which content depend on MACHINE_FEATURES need to be MACHINE_ARCH
#
PACKAGE_ARCH	 = "${MACHINE_ARCH}"

#
# udev, devfsd, mdev (from busybox) or none
#
DISTRO_DEV_MANAGER ?= "${@base_contains("MACHINE_FEATURES", "kernel26",  "udev","",d)} "

OVERRIDES .= "${@base_contains("DISTRO_DEV_MANAGER", "udev", ":udev", "", d)}"

EXTRA_DEV_RULES ?=
EXTRA_DEV_RULES_append_udev = " ${@base_contains("MACHINE_FEATURES","firmware","udev-firmware","",d)}"
EXTRA_DEV_RULES_append_udev = " ${@base_contains("MACHINE_FEATURES","modules","udev-rules-modules","",d)}"

#
# sysvinit, upstart
#
DISTRO_INIT_MANAGER ?= "sysvinit"

#
# tinylogin, getty
#
DISTRO_LOGIN_MANAGER ?= "tinylogin"

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
    ${@base_contains("MACHINE_FEATURES", "modules",  "virtual/modutils-initscripts", "", d)} \
    virtual/initscripts 		\
    ${EXTRA_DEV_RULES}			\
    ${DISTRO_DEV_MANAGER} 		\
    ${DISTRO_INIT_MANAGER} 		\
    ${DISTRO_LOGIN_MANAGER} 		\
    ${PROJECT_EXTRA_RDEPENDS}		\
    "

RRECOMMENDS_${PN} += "\
    ${PROJECT_EXTRA_RRECOMMENDS}	\
    "
