FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI += " \
	file://misc.patch \
	file://pxa320.patch"

do_compile() {
    for c in 270 320; do
        ${CC} pxaregs.c -o pxaregs$c ${CFLAGS} ${LDFLAGS} -D PXA$c
    done
}

do_install() {
    install -d ${D}${sbindir}
    install -p -m 0755 pxaregs270 pxaregs320 ${D}${sbindir}/
}

PACKAGES = "${PN}-dbg pxaregs270 pxaregs320 pxaregs"
FILES_pxaregs270 = "${sbindir}/*270"
FILES_pxaregs320 = "${sbindir}/*320"
