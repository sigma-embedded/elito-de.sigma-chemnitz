SUMMARY = "Converter for FreeScale IOMUx Tool files"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"

RDEPENDS_${PN} += "libxslt bash"

BBCLASSEXTEND = "native"

SRC_URI = "\
    file://fsliomux-conv \
    file://iomux-macrofy \
    file://iomux2c.xsl \
    file://iomux2dt.xsl \
"

pkglibdir = "${libdir}/${BPN}"

do_configure() {
    sed -i \
	-e 's!PKGLIBDIR=/usr/lib/fsliomux-conv!PKGLIBDIR=${pkglibdir}!g' \
	${WORKDIR}/fsliomux-conv
}

do_install() {
    install -d -m 0755 ${D}${bindir} ${D}${libdir}/${BPN}

    cd ${WORKDIR}
    install -p -m 0755 fsliomux-conv ${D}${bindir}/
    install -p -m 0755 iomux-macrofy ${D}${pkglibdir}/
    install -p -m 0644 iomux2c.xsl ${WORKDIR}/iomux2dt.xsl ${D}${pkglibdir}/
    cd -
}
