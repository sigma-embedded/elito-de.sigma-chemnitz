SECTION = "bootloader"
PACKAGE_ARCH = "${MACHINE_ARCH}"

SRCREV = "${AUTOREV}"

UBOOT_BRANCH	??= "${KERNEL_BRANCH}"
UBOOT_MACHINE	??= "${MACHINE}_config"
UBOOT_REV	??= "branch=${UBOOT_VERSION}/${UBOOT_BRANCH}"
UBOOT_SYMLINK	??= "u-boot-${MACHINE}"
UBOOT_CONSOLE	??= "${@'${SERIAL_CONSOLE}'.split()[1]}"
UBOOT_BAUD	??= "${@'${SERIAL_CONSOLE}'.split()[0]}"
UBOOT_REPO	??= "git+file://${ELITO_GIT_WS}/${_repo}"

PV   = "${UBOOT_VERSION}+gitr${SRCPV}"
PKGV = "${UBOOT_VERSION}+gitr${GITPKGV}"

SRC_URI = "${UBOOT_REPO};${UBOOT_REV}"

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
