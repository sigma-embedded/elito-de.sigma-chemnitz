SUMMARY = "ELiTo image stream tools"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"

_pv     = "0.1.3"
PR      = "r0"

SRCREV  = "bd126161b10f0db3a37c96780a80957f1336e965"
SRC_URI = "${ELITO_GIT_REPO}/pub/elito-image-stream.git"

PV   = "${_pv}+gitr${SRCPV}"
PKGV = "${_pv}+gitr${GITPKGV}"

inherit gitpkgv autotools

REQUIRES += "gnutls"

EXTRA_OEMAKE += "\
  prefix=${prefix} \
  bindir=${bindir} \
  progprefix=elito- \
"

S = "${WORKDIR}/git"

PACKAGES =+ "${PN}-encode ${PN}-decode"
FILES_${PN}-encode = "${bindir}/*-encode"
FILES_${PN}-decode = "${bindir}/*-decode"

BBCLASSEXTEND = "native"

