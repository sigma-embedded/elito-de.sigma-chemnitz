DESCRIPTION  = "${MACHINE} device tree"
LICENSE      = "GPLv3"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"

PACKAGE_ARCH = "${MACHINE_ARCH}"

def get_dts_files(d):
    return ' '.join(map(lambda x: '%s' % x.split(':')[0],
                        oe.data.typed_value('MACHINE_VARIANTS', d)))

_machine_dts_name[vardeps] += "MACHINE_VARIANTS"
_machine_dts_name     = "${MACHINE}"
_machine_dts_name_mx6 = "${@get_dts_files(d)}"

MACHINE_DTS_NAME ?= "${_machine_dts_name}"
MACHINE_DTS_NAME[type] = "list"

_dtb_prefix = ""
_dtb_prefix_rpi = "${KERNEL_IMAGETYPE}-"

DTB_PREFIX ?= "${_dtb_prefix}"

SRC_URI[vardeps] += "MACHINE_DTS_NAME"
SRC_URI = "\
  ${@' '.join(map(lambda x: 'file://%s.dts' % x, \
                  oe.data.typed_value('MACHINE_DTS_NAME', d)))} \
"

INHIBIT_DEFAULT_DEPS = "1"
EXTRA_OEMAKE = "\
  -f ${STAGING_DIR_NATIVE}/${datadir}/elito-devicetree-tools/devicetree.mk \
  abs_top_srcdir='${S}' \
  MACHINE=${MACHINE} \
  prefix=${prefix} datadir=${datadir} \
  pkgdatadir=${MACHDATADIR} \
  KERNEL_DIR=${STAGING_KERNEL_DIR} \
  MACHINE_INCDIR=${MACHINCDIR} \
  VARIANTS='${MACHINE_DTS_NAME}' \
"

EXTRA_OEMAKE_append_mx28 = " SOC_FAMILY=mx28"

COMPATIBLE_MACHINE = "(mx28|mx6|mx7|ti33x|raspberrypi2)"

MACH_DEPENDS = ""
MACH_DEPENDS_mx28 = "mx28-pins"
MACH_DEPENDS_mx6 = "${@bb.utils.contains('DISTRO_FEATURES', 'fsl-iomux', \
                     'mx6-pins', '', d)}"

DEPENDS += "dtc-native ${MACH_DEPENDS} virtual/${TARGET_PREFIX}gcc"
DEPENDS += "elito-devicetree-tools-cross-${TARGET_ARCH}"

B := "${S}"
S  = "${WORKDIR}"

inherit deploy elito-machdata elito-dtree-base

do_emit_buildvars[depends] += "virtual/kernel:do_patch"

do_compile[depends] += "virtual/kernel:do_patch"
do_compile() {
    oe_runmake -e
}

do_install() {
    oe_runmake -e install DESTDIR=${D}
}

do_deploy() {
    for i in *.dtb; do
	dname=${i%%.dtb}-"${EXTENDPKGV}".dtb
	install -D -p -m 0644 "$i" ${DEPLOYDIR}/"$dname"
	rm -f ${DEPLOYDIR}/"${DTB_PREFIX}$i"
	ln -s "$dname" ${DEPLOYDIR}/"${DTB_PREFIX}$i"
    done
}
addtask deploy before do_package after do_compile

FILES_${PN}-dev += "${MACHDATADIR}/*.dtb  ${MACHDATADIR}/*.mk"
RDEPENDS_${PN}-dev += "make"
