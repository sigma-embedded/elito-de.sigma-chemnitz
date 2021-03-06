_elito_skip := "${@elito_skip(d, None, 'nokernel')}"

DESCRIPTION = "Tools for generating device trees within an ELiTo project"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"

MACHDEPS = ""
MACHDEPS_mx6 = "\
  ${@bb.utils.contains('DISTRO_FEATURES', 'fsl-iomux', \
                       'fsliomux-conv mx6-pins', '', d)} \
"

DEPENDS += "dtc-native ${MACHDEPS}"

inherit elito-machdata
inherit elito-makefile-component

BBCLASSEXTEND = "cross crosssdk"

SRC_URI = "\
  file://devicetree.mk \
  file://build-dtree \
"

KERNEL_DTREE_DIR     = "${STAGING_KERNEL_DIR}"
KERNEL_DTREE_DIR_arm = "${STAGING_KERNEL_DIR}/arch/arm/boot/dts"

python () {
    override = d.getVar("CLASSOVERRIDE", True) or ""
    if override == "class-cross":
        d.appendVar("PN", "-${TARGET_ARCH}")
        d.setVar("SPECIAL_PKGSUFFIX", "-cross-${TARGET_ARCH}")
}

B := "${S}"
S  = "${WORKDIR}"

pkgdatadir = "${datadir}/${BPN}"

do_configure[vardeps] += "SOC_FAMILY"
do_configure() {
    rm -f build-dtree
    sed \
	-e 's!@STAGINGDIR@!${STAGING_DIR_TARGET}!g' \
	-e 's!@MACHINCDIR@!${MACHINCDIR}!g' \
	-e 's!@MACHDATADIR@!${MACHDATADIR}!g' \
	-e 's!@TFTPBOOT_DIR@!${TFTPBOOT_DIR}!g' \
	-e 's!@PROJECT_TOPDIR@!${PROJECT_TOPDIR}!g' \
	-e 's!@KERNEL_DIR@!${STAGING_KERNEL_DIR}!g' \
	-e 's!@KERNEL_DTREE_DIR@!${KERNEL_DTREE_DIR}!g' \
	-e 's!@PKGDATA_DIR@!${pkgdatadir}!g' \
	-e 's!@SOC@!${@(d.getVar("SOC_FAMILY", True) or "").split(":")[0]}!g' \
	${S}/build-dtree > build-dtree

    touch -r ${S}/build-dtree build-dtree || :
}

# hack; use the override because do_install() from BBCLASSEXTEND seems
# to win else
do_install_forcevariable() {
    install -D -p -m 0755 build-dtree ${D}${bindir}/elito-build-dtree
    install -D -p -m 0644 ${WORKDIR}/devicetree.mk ${D}${pkgdatadir}/devicetree.mk
}
addtask do_install before do_populate_sysroot after do_configure

do_build[depends] += "virtual/kernel:do_patch"

RDEPENDS_${PN} += "bash"
