SECTION = "bootloader"
PACKAGE_ARCH = "${MACHINE_ARCH}"
PV = "${UBOOT_VERSION}"
INCPR = "r0"

SRCREV           = "${AUTOREV}"

UBOOT_BRANCH	?= "${KERNEL_BRANCH}"
UBOOT_MACHINE	?= "${MACHINE}_config"
UBOOT_REV       ?= "branch=${UBOOT_VERSION}/${UBOOT_BRANCH}"
UBOOT_SYMLINK	?= "u-boot-${MACHINE}.bin"
UBOOT_CONSOLE	?= "${@'${SERIAL_CONSOLE}'.split()[1]}"
UBOOT_BAUD	?= "${@'${SERIAL_CONSOLE}'.split()[0]}"

SRCURI_SPEC_append	=  ";${UBOOT_REV}"
COMPONENT = "u-boot"

inherit elito-develcomp

S = "${WORKDIR}/git"
