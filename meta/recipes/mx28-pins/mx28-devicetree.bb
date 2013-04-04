DESCRIPTION  = "${MACHINE} device tree"
LICENSE      = "GPLv3"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"

SRC_URI = "\
  file://Makefile \
  file://${MACHINE}.dts \
"

INHIBIT_DEFAULT_DEPS = "1"
EXTRA_OEMAKE = "\
  MACHINE=${MACHINE} \
  VPATH=${WORKDIR} prefix=${prefix} datadir=${datadir} \
"

COMPATIBLE_MACHINE = "mx28"
PACKAGE_ARCH = "${MACHINE_ARCH}"
DEPENDS += "elito-kernel dtc-native mx28-pins"

do_configure() {
    ln -sf ../Makefile '.'
    ln -sf ../${MACHINE}.dts '.'
    ln -sf ${STAGING_DATADIR}/mach-${MACHINE}/*.dtsi '.'
}

do_install() {
    oe_runmake install DESTDIR=${D} 
}

FILES_${PN}-dev += "${datadir}/mach-${MACHINE}/*.dtb"
