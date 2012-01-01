DESCRIPTION  = "@PROJECT_NAME@ files"
LICENSE      = "GPLv3"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=2c12447f794c304d9cd353f87a432c9e"

PACKAGE_ARCH = "${MACHINE_ARCH}"
SRCREV       = "${PROJECT_REVISION}"

PV = "0.1+gitr${SRCPV}"
PR = "r0"

SRC_URI = "git://${PROJECT_TOPDIR};protocol=file"

FILES_${PN} = "/"

# for PROJECT_REVISION
inherit elito-utils

do_fetch[noexec] = "1"
do_unpack[noexec] = "1"
do_distribute_sources[noexec] = "1"

do_compile() {
        make -C ${FILE_DIRNAME} dist TARBALL=`pwd`/base-files.tar.bz2
}

do_install() {
        install -d ${D}
        tar xjf base-files.tar.bz2 -C ${D}
}
