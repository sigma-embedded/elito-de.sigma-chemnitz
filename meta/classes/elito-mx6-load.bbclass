SRCREV_FORMAT = "default"
SRC_URI += "${ELITO_GIT_REPO}/pub/elito-mx6-load.git;name=mx6-load;destsuffix=mx6-load/"

DEPENDS += "barebox"

do_compile_append() {
    oe_runmake -C ${WORKDIR}/mx6-load \
	KERNEL_IMAGE=${WORKDIR}/git/arch/arm/boot/zImage \
	MACHTYPE=4634 MACHINE=${MACHINE}
}

do_deploy_append() {
    cd ${WORKDIR}/mx6-load
    # todo
    install -p -m 0644 load-linux.bin ${DEPLOYDIR}/${KERNEL_IMAGE_BASE_NAME}.imx
    cd -

    cd "${DEPLOYDIR}"
    rm -f ${KERNEL_IMAGE_SYMLINK_NAME}.imx
    ln -sf ${KERNEL_IMAGE_BASE_NAME}.imx ${KERNEL_IMAGE_SYMLINK_NAME}.imx
    cd -
}
