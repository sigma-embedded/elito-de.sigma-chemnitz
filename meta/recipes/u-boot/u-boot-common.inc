SECTION = "bootloader"
PACKAGE_ARCH = "${MACHINE_ARCH}"

SRCREV = "${AUTOREV}"

UBOOT_BRANCH	?= "${KERNEL_BRANCH}"
UBOOT_MACHINE	?= "${MACHINE}_config"
UBOOT_REV       ?= "branch=${UBOOT_VERSION}/${UBOOT_BRANCH}"
UBOOT_SYMLINK	?= "u-boot-${MACHINE}.bin"
UBOOT_CONSOLE	?= "${@'${SERIAL_CONSOLE}'.split()[1]}"
UBOOT_BAUD	?= "${@'${SERIAL_CONSOLE}'.split()[0]}"
UBOOT_MACHINE   ?= "${KERNEL_DEFCONFIG}"

PV = "${UBOOT_VERSION}+gitr${SRCPV}"
PKGV = "${UBOOT_VERSION}+gitr${GITPKGV}"

SRC_URI = "${ELITO_GIT_REPO}/${_repo}.git;protocol=git;${UBOOT_REV}"

OVERRIDES .= "${@bb.utils.contains('MACHINE_FEATURES', 'imx-bootlets',\
                                   ':img-imx-bootlets','',d)}"

DEPLOYDEPENDS = ""
DEPLOYDEPENDS_img-imx-bootlets = "\
 elftosb-native:do_populate_sysroot \
 imx-bootlets:do_populate_sysroot"

inherit gitpkgv

S = "${WORKDIR}/git"

do_builddeploy[depends] = "${DEPLOYDEPENDS}"
do_builddeploy() {
    :
}
