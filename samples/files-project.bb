DESCRIPTION  = "@PROJECT_NAME@ files"
LICENSE      = "GPLv3"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"

PACKAGE_ARCH = "${MACHINE_ARCH}"

PV = "0.1"

FILES_${PN} = "/"
ALLOW_EMPTY_${PN} = "1"

ELITO_COPY_TOPDIRS = "rootfs"

inherit elito-copytree
