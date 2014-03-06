SUMMARY = "rescue system updater"
LICENSE = "GPLv3"
DEPENDS = "libccgi"

LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"

_pv     = "0.2.6"
PR      = "r0"

SRCREV  = "639f04d60dce8c9055c64c6b6fa624fe68a8cb02"
SRC_URI = "${ELITO_GIT_REPO}/pub/elito-rescue-utils.git"

PV   = "${_pv}+gitr${SRCPV}"
PKGV = "${_pv}+gitr${GITPKGV}"

inherit gitpkgv autotools

wwwdir = "/srv/www"

EXTRA_OEMAKE = " \
  LIBS=-lccgi \
  datadir=${datadir} \
  bindir=${bindir} \
  wwwdir=${wwwdir}"

S = "${WORKDIR}/git"

do_configure_prepend() {
    sed -i \
        -e 's!@PROJECT@!${PROJECT_NAME}!g' \
        ${S}/cgi/index.html
}

RDEPENDS_${PN} += "elito-image-stream-decode virtual/rescue-conf"

PACKAGES += "${PN}-http"

FILES_${PN}-dbg += "/srv/www/cgi-bin/.debug"

FILES_${PN} = "${datadir}/elito-rescue ${bindir}/*"

RDEPENDS_${PN}-http += "${PN}"
FILES_${PN}-http += "\
  ${wwwdir}/index.html \
  ${wwwdir}/cgi-bin/image-update.cgi \
"
