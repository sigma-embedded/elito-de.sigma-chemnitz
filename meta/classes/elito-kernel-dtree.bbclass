ELITO_DTS_DIR ?= "arch/arm/boot/dts"
ELITO_DTS_FILES ?= ""

do_installdts() {
    install -d -m 0755 ${D}${datadir}/mach-${MACHINE}

    cd "${ELITO_DTS_DIR}"
    install -p -m 0644 ${ELITO_DTS_FILES} ${D}${datadir}/mach-${MACHINE}/
    cd -
}

do_install_append() {
    do_installdts
}

python do_sysroot_installdts() {
    oe.path.copyhardlinktree(
        d.expand("${D}${datadir}/mach-${MACHINE}/"),
        d.expand("${SYSROOT_DESTDIR}${datadir}/mach-${MACHINE}/"))
}

SYSROOT_PREPROCESS_FUNCS += "do_sysroot_installdts"

PACKAGES =+ "${PN}-dtree"
FILES_${PN}-dtree = "${datadir}/mach-${MACHINE}/*.dtsi"
