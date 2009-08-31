DESCRIPTION  = "@PROJECT_NAME@ files"
LICENSE      = "proprietary"
PACKAGE_ARCH = "${MACHINE_ARCH}"
SRCREV       = "${PROJECT_REVISION}"

PV = "0.1+gitr${SRCPV}"
PR = "r0"

SRC_URI = "git://${PROJECT_TOPDIR};protocol=file"

do_fetch() {
}

do_unpack() {
}

do_distribute_sources() {
}

do_compile() {
        make -C ${FILE_DIRNAME} dist TARBALL=`pwd`/base-files.tar.bz2
}

FILES_${PN} = "/"

do_install() {
        install -d ${D}
        tar xjf base-files.tar.bz2 -C ${D}
}
