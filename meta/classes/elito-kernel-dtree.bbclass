ELITO_DTS_DIR ?= "arch/arm/boot/dts"
ELITO_DTS_FILES ?= ""

inherit elito-machdata

do_installdts() {
    install -d -m 0755 ${D}${MACHINCDIR}/

    cd "${ELITO_DTS_DIR}"
    install -p -m 0644 ${ELITO_DTS_FILES} ${D}${MACHINCDIR}/
    cd -
}

do_install_append() {
    do_installdts
}

python do_sysroot_installdts() {
    oe.path.copyhardlinktree(
        d.expand("${D}${MACHINCDIR}/"),
        d.expand("${SYSROOT_DESTDIR}${MACHINCDIR}/"))
}

SYSROOT_PREPROCESS_FUNCS += "do_sysroot_installdts"

PACKAGES =+ "${PN}-dtree"
FILES_${PN}-dtree = " \
  ${MACHINCDIR}/*.dtsi \
  ${MACHINCDIR}/*.h \
"
