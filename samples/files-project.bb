DESCRIPTION  = "@PROJECT_NAME@ files"
LICENSE      = "GPLv3"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"

PACKAGE_ARCH = "${MACHINE_ARCH}"

PV = "0.1"
PR = "r0"

FILES_${PN} = "/"
ALLOW_EMPTY_${PN} = "1"

# for PROJECT_REVISION
inherit elito-utils
inherit elito-rebuild

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
