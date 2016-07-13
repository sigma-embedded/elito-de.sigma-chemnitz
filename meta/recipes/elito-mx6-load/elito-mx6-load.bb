_elito_skip := "${@elito_skip(d, 'rescuekernel', None, 'PROJECT_FEATURES')}"

SUMMARY = "imx6 rescue system loader"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"

_pv     = "0.1.0"

PV   = "${_pv}+gitr${SRCPV}"
PKGV = "${_pv}+gitr${GITPKGV}"

PACKAGE_ARCH = "${MACHINE_ARCH}"
COMPATIBLE_MACHINE = "mx6"

MX6_LOAD_BRANCH ??= "master"
MX6_LOAD_GIT_REPO ??= "${ELITO_GIT_REPO}/elito-mx6-load.git;branch=${MX6_LOAD_BRANCH}"

SRC_URI = "\
  ${MX6_LOAD_GIT_REPO};destsuffix=mx6-load/ \
"

inherit gitpkgv

DEPENDS += "\
  ${@bb.utils.contains('MACHINE_FEATURES', 'barebox', 'barebox', '', d)} \
  elito-devicetree \
  elito-rescue-kernel \
"

MX6LOAD_VARIANTS ?= "${@mx6_load_get_variants(d)}"
MX6LOAD_SOCTYPES ?= "${@mx6_load_get_soctypes(d)}"
KERNEL_MACHTYPE ?= "0xffffffff"

EXTRA_MX6LOAD_FLAGS ?= ""

MX6LOAD_IMAGE_BASE_NAME ?= "rescue${@(lambda x: ['','-%s' % x][x != ''])(d.getVar('PKGE', True))}-${PKGV}-${PKGR}-${MACHINE}-${DATETIME}"
MX6LOAD_IMAGE_BASE_NAME[vardepsexclude] = "DATETIME"
MX6LOAD_IMAGE_SYMLINK_NAME ?= "rescue-${MACHINE}"

mx6_load_get_variants[vardeps] += "MACHINE_VARIANTS"
def mx6_load_get_variants(d):
    variants = oe.data.typed_value('MACHINE_VARIANTS', d)
    vars = []
    for v in variants:
        (board, soc, mem) = v.split(':')
        vars.append('%s-%s' % (board, mem))
    return ' '.join(vars)

mx6_load_get_soctypes[vardeps] += "MACHINE_VARIANTS"
def mx6_load_get_soctypes(d):
    variants = oe.data.typed_value('MACHINE_VARIANTS', d)
    socs = set()
    for v in variants:
        (board, soc, mem) = v.split(':')
        socs.add(board)
    return ' '.join(socs)

do_compile[depends] += "elito-rescue-kernel:do_deploy"
do_compile() {
    # we can not call 'oe_runmake' here because the EXTRA_OEMAKE flags
    # are not suitable for non-kernel builds

    make -f ${WORKDIR}/mx6-load/Makefile \
	KERNEL_IMAGE=${DEPLOY_DIR_IMAGE}/rescue-zImage-${MACHINE}.bin \
	DCDPATH='${STAGING_DIR_TARGET}${datadir}/mach-${MACHINE}' \
	MACHINE='${MACHINE}' MACHTYPE='${KERNEL_MACHTYPE}' \
        VARIANTS='${MX6LOAD_VARIANTS}' \
	SOC_TYPES='${MX6LOAD_SOCTYPES}' \
        ${EXTRA_MX6LOAD_FLAGS}
}

inherit deploy

do_deploy() {
    for v in ${MX6LOAD_VARIANTS}; do
        install -p -m 0644 load-linux-$v.bin ${DEPLOYDIR}/${MX6LOAD_IMAGE_BASE_NAME}-$v.imx
	rm -f '${DEPLOYDIR}/${MX6LOAD_IMAGE_SYMLINK_NAME}'-$v.imx
        ln -sf ${MX6LOAD_IMAGE_BASE_NAME}-$v.imx '${DEPLOYDIR}/${MX6LOAD_IMAGE_SYMLINK_NAME}'-$v.imx
    done
}
addtask deploy before do_build after do_compile
