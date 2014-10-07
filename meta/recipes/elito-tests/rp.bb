SECTION  = "base"
DESCRIPTION = "ELiTo Sound Test utilities"
PV = "0.0.2"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"

PACKAGE_ARCH = "all"

do_compile() {
    :
}

do_install() {
    mkdir ${D}/bin
    ln -s ../../../busybox ${D}/bin/XXX
    ln -s Xbusybox ${D}/bin/YYY
}

RDEPENDS_${PN} += "busybox"
