DESCRIPTION = "Basic task to get a device booting"
PR          = "r3"

inherit task

# packages which content depend on MACHINE_FEATURES need to be MACHINE_ARCH
#
PACKAGE_ARCH	 = "${MACHINE_ARCH}"

#
# udev, devfsd, mdev (from busybox) or none
#
DISTRO_DEV_MANAGER ?= "${@base_contains("MACHINE_FEATURES", "kernel26",  "udev","",d)} "

#
# sysvinit, upstart
#
DISTRO_INIT_MANAGER ?= "sysvinit"

#
# tinylogin, getty
#
DISTRO_LOGIN_MANAGER ?= "tinylogin"

#
# those ones can be set in machine config to supply packages needed to get machine booting
#
MACHINE_ESSENTIAL_EXTRA_RDEPENDS ?= ""
MACHINE_ESSENTIAL_EXTRA_RRECOMMENDS ?= ""

# Make sure we build the kernel
DEPENDS = "virtual/kernel"

#
# minimal set of packages - needed to boot
#
RDEPENDS_elito-task-boot = "\
    virtual/base-files \
    base-passwd \
    busybox \
    virtual/initscripts \
    ${@base_contains("MACHINE_FEATURES", "ubifs", "mtd-utils", "", d)} \
    ${@base_contains("MACHINE_FEATURES", "keyboard", "keymaps", "", d)} \
    ${@base_contains("MACHINE_FEATURES", "modules", \
		     "virtual/modutils-initscripts module-init-tools", "", d)} \
    update-alternatives \
    virtual/netbase	\
    ${DISTRO_DEV_MANAGER} \
    ${DISTRO_INIT_MANAGER} \
    ${DISTRO_LOGIN_MANAGER} \
    ${MACHINE_ESSENTIAL_EXTRA_RDEPENDS} \
    "

RRECOMMENDS_elito-task-boot = "\
    ${MACHINE_ESSENTIAL_EXTRA_RRECOMMENDS} \
    "

PACKAGES = "${PN}"
