SUMMARY = "ELiTo image stream tools"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"

_pv     = "0.2.3"

SRCREV  = "f1f8f79c63168b7f5ca3e62d1f1cdedd4b4985d0"
SRC_URI = "${ELITO_GIT_REPO}/pub/elito-image-stream.git"

PV   = "${_pv}+gitr${SRCPV}"
PKGV = "${_pv}+gitr${GITPKGV}"

inherit gitpkgv autotools-brokensep

PACKAGECONFIG ??= "digest-gnutls x509-gnutls compression-noop"
PACKAGECONFIG_virtclass-native = "digest-gnutls x509-gnutls compression-noop"

PACKAGECONFIG[digest-gnutls] = "DIGEST_PROVIDER=gnutls,,gnutls,"
PACKAGECONFIG[digest-kernel] = "DIGEST_PROVIDER=kernel,,,"
PACKAGECONFIG[x509-gnutls]   = "X509_PROVIDER=gnutls,,gnutls,"
PACKAGECONFIG[x509-noop]     = "X509_PROVIDER=noop,,,"
PACKAGECONFIG[compression-noop] = "COMPRESSION_PROVIDER=noop,,,"

REQUIRES += "gnutls"

EXTRA_OEMAKE += "\
  prefix=${prefix} \
  bindir=${bindir} \
  progprefix=elito- \
  ${EXTRA_OECONF} \
"

S = "${WORKDIR}/git"

PACKAGES =+ "${PN}-encode ${PN}-decode"
FILES_${PN}-encode = "${bindir}/*-encode"
FILES_${PN}-decode = "${bindir}/*-decode"

BBCLASSEXTEND = "native"

