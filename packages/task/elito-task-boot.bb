DESCRIPTION = "Basic task to get a device booting to a prompt"
PR          = "r9.${PROJECT_FILE_DATE}"

inherit task

# packages which content depend on MACHINE_FEATURES need to be MACHINE_ARCH
#
PACKAGE_ARCH	 = "${MACHINE_ARCH}"

#
# those ones can be set in machine config to supply packages needed to get machine booting
#
MACHINE_ESSENTIAL_EXTRA_RDEPENDS ?= ""
MACHINE_ESSENTIAL_EXTRA_RRECOMMENDS ?= ""

DISTRO_ESSENTIAL_EXTRA_RDEPENDS ?= ""
DISTRO_ESSENTIAL_EXTRA_RRECOMMENDS ?= ""

PROJECT_EXTRA_RDEPENDS ?= ""
PROJECT_EXTRA_RRECOMMENDS ?= ""

# Make sure we build the kernel
DEPENDS = "virtual/kernel"

#
# minimal set of packages - needed to boot
#
RDEPENDS_${PN} += "\
    virtual/base-files \
    base-passwd \
    busybox \
    ${@base_contains("MACHINE_FEATURES", "ubifs",   "mtd-utils-ubi-core", "", d)} \
    ${@base_contains("MACHINE_FEATURES", "modules", "module-init-tools",  "", d)} \
    update-alternatives \
    virtual/netbase	\
    ${MACHINE_ESSENTIAL_EXTRA_RDEPENDS} \
    ${DISTRO_ESSENTIAL_EXTRA_RDEPENDS}	\
    "

RRECOMMENDS_${PN} += "\
    ${MACHINE_ESSENTIAL_EXTRA_RRECOMMENDS}	\
    ${DISTRO_ESSENTIAL_EXTRA_RRECOMMENDS}	\
    "
