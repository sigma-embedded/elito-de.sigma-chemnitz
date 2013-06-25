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
  MACHINE_INCDIR=${STAGING_DATADIR}/mach-${MACHINE} \
"

EXTRA_OEMAKE_append_mx28 = " SOC_FAMILY=mx28"

COMPATIBLE_MACHINE = "(mx28|mx6)"
PACKAGE_ARCH = "${MACHINE_ARCH}"

MACH_DEPENDS = ""
MACH_DEPENDS_mx28 = "mx28-pins"

DEPENDS += "elito-kernel dtc-native ${MACH_DEPENDS}"

inherit deploy

do_configure() {
    rm -f *.dts *.dtsi Makefile
    ln -s ../Makefile '.'
}

do_install() {
    oe_runmake install DESTDIR=${D}
}

do_deploy() {
    install -D -p -m 0644 *.dtb ${DEPLOYDIR}/oftree
}

FILES_${PN}-dev += "${datadir}/mach-${MACHINE}/*.dtb"
