DESCRIPTION = "Tools for generating device trees within an ELiTo project"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"

MACHDEPS = ""
MACHDEPS_mx6 = "\
  fsliomux-conv \
  ${@bb.utils.contains('DISTRO_FEATURES', 'fsl-iomux', \
                      'mx6-pins', '', d)} \
"

DEPENDS += "dtc-native elito-devicetree ${MACHDEPS}"

inherit elito-machdata

BBCLASSEXTEND = "cross crosssdk"

SRC_URI = "file://build-dtree"

KERNEL_DTREE_DIR     = "${STAGING_KERNEL_DIR}"
KERNEL_DTREE_DIR_arm = "${STAGING_KERNEL_DIR}/arch/arm/boot/dts"

B := "${S}"
S  = "${WORKDIR}"

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
	-e 's!@SOC@!${@(d.getVar("SOC_FAMILY", True) or "").split(":")[0]}!g' \
	${S}/build-dtree > build-dtree

    touch -r ${S}/build-dtree build-dtree || :
}

# hack; use the _pn-${PN} override because do_install() from BBCLASSEXTEND
# seems to win else
do_install_pn-${PN}() {
    install -D -p -m 0755 build-dtree ${D}${bindir}/elito-build-dtree
}

do_build[depends] += "virtual/kernel:do_patch"

RDEPENDS_${PN} += "bash"
