SRCREV_FORMAT = "default"
SRC_URI += "${ELITO_GIT_REPO}/pub/elito-mx6-load.git;name=mx6-load;destsuffix=mx6-load/"

DEPENDS += "barebox elito-devicetree"

MX6LOAD_VARIANTS ?= "${@mx6_load_get_variants(d)}"
MX6LOAD_SOCTYPES ?= "${@mx6_load_get_soctypes(d)}"
KERNEL_MACHTYPE ?= "0xffffffff"

EXTRA_MX6LOAD_FLAGS ?= ""

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

do_compile_mx6load() {
    # we can not call 'oe_runmake' here because the EXTRA_OEMAKE flags
    # are not suitable for non-kernel builds

    make -f ${WORKDIR}/mx6-load/Makefile \
	KERNEL_IMAGE=${B}/arch/arm/boot/zImage \
	DCDPATH='${STAGING_DIR_TARGET}${datadir}/mach-${MACHINE}' \
	MACHINE='${MACHINE}' MACHTYPE='${KERNEL_MACHTYPE}' \
        VARIANTS='${MX6LOAD_VARIANTS}' \
	SOC_TYPES='${MX6LOAD_SOCTYPES}' \
        ${EXTRA_MX6LOAD_FLAGS}
}
addtask do_compile_mx6load after do_compile before do_deploy

do_deploy_append() {
    for v in ${MX6LOAD_VARIANTS}; do
        install -p -m 0644 load-linux-$v.bin ${DEPLOYDIR}/${KERNEL_IMAGE_BASE_NAME}-$v.imx
	rm -f '${DEPLOYDIR}/${KERNEL_IMAGE_SYMLINK_NAME}'-$v.imx
        ln -sf ${KERNEL_IMAGE_BASE_NAME}-$v.imx '${DEPLOYDIR}/${KERNEL_IMAGE_SYMLINK_NAME}'-$v.imx
    done
}
