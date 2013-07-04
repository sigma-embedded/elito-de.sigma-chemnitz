DESCRIPTION  = "${MACHINE} device tree"
LICENSE      = "GPLv3"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"

SRC_URI = "\
  file://Makefile \
  file://${MACHINE}.dts \
"

OLDNAME = ""
OLDNAME_mx28 = "mx28-devicetree"

PROVIDES_mx28 += "${OLDNAME}"
REPLACES_mx28 += "${OLDNAME}"
RREPLACES_${PN}-dev += "${OLDNAME}-dev"

INHIBIT_DEFAULT_DEPS = "1"
EXTRA_OEMAKE = "\
  -f ${WORKDIR}/Makefile \
  MACHINE=${MACHINE} \
  prefix=${prefix} datadir=${datadir} \
  MACHINE_INCDIR=${STAGING_DATADIR}/mach-${MACHINE} \
"

EXTRA_OEMAKE_append_mx28 = " SOC_FAMILY=mx28"

COMPATIBLE_MACHINE = "(mx28|mx6)"
PACKAGE_ARCH = "${MACHINE_ARCH}"

MACH_DEPENDS = ""
MACH_DEPENDS_mx28 = "mx28-pins"

DEPENDS += "elito-kernel dtc-native ${MACH_DEPENDS}"

inherit deploy

do_install() {
    oe_runmake install DESTDIR=${D}
}

do_deploy() {
    install -D -p -m 0644 *.dtb ${DEPLOYDIR}/oftree
}

FILES_${PN}-dev += "${datadir}/mach-${MACHINE}/*.dtb"
