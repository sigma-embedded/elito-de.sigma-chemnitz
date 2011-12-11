DESCRIPTION = "Initial bootloader for PXA320"
SECTION = "bootloaders"
PRIORITY = "optional"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"
PACKAGE_ARCH = "${MACHINE_ARCH}"
_pv = "0.3.12"
PR = "r1"
PV = "${_pv}"

MOBM320_IMAGE ?=
MOBM320_ARGS ?=

MOBM320_IMAGE_BASE_NAME ?= "mobm320-${PV}-${PR}-${MACHINE}"
MOBM320_IMAGE_SYMLINK_NAME ?= "mobm320-${MACHINE}"

SRC_URI = "${ELITO_MIRROR}/elito-mobm320-${_pv}.tar.xz"
S = "${WORKDIR}/elito-mobm320-${PV}"

SRC_URI[md5sum] = "962112c0df70b0d8bf7316eb90f2ceb1"
SRC_URI[sha256sum] = "058c0c8cf6acc38a5fac4495637df9816094bd6a6b3dc047bc3630ef2fcfbc13"
