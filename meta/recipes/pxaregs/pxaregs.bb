_o = "${OEMETA_TOPDIR}/meta-oe/recipes-support/pxaregs"

PV = "1.14"
require ${_o}/pxaregs_${PV}.bb

PE = "1"
DEFAULT_PREFERENCE = "99"

FILESPATHBASE_append = ":${_o}"

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
