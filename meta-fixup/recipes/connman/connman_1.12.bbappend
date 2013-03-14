INITSCRIPT_PACKAGES = "${PN}-sysv"

do_install_append() {
	install -d -m 0755 ${D}${localstatedir}/lib/connman
}

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI += "\
  file://0001-added-noipconfig-option.patch \
"

DEPENDS += "readline"

SYSTEMD_PACKAGES = "${PN}-systemd"
PACKAGES += "${SYSTEMD_PACKAGES}"
FILES_${PN}-systemd += "${systemd_unitdir}/*"
RDEPENDS_${PN}-systemd += "${PN} (= ${EXTENDPKGV})"
RRECOMMENDS_${PN} += "${SYSTEMD_PACKAGES}"
