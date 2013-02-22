DESCRIPTION  = "ELiTo QT demos"
LICENSE      = "GPLv3"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"

SRC_URI = "\
  file://demo \
  file://demo-pinchzoom.service \
  file://demo-fingerpaint.service \
  file://demo-qtdemoE.service \
"

SYSTEMD_SERVICE_${PN}-systemd = "demo-pinchzoom.service"
SYSTEMD_PACKAGES = "${PN}-systemd"
PACKAGES =+ "${PN}-systemd"

do_install() {
    cd ${WORKDIR}
    install -D -p -m 0755 demo ${D}${bindir}/elito-qt-demo

    for i in *.service; do
	install -D -p -m 0644 $i ${D}${systemd_unitdir}/system/$i
    done
}

inherit systemd

RDEPENDS_${PN} += "qt4-embedded-examples"
FILES_${PN}-systemd = "${systemd_unitdir}/system/*"
FILES_${PN} += "${bindir}/*"
