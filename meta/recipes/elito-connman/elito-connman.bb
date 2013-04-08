SUMMARY = "Sample programs demonstrating setup of connman"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"

SRCREV = "af0952cf75d6e976115bf15c6859272009acda76"

_pv = "0.0.1"
PR = "r0"
PV = "${_pv}+gitr${SRCPV}"

inherit gitpkgv autotools

DEPENDS = "dbus-glib"
SRC_URI = "${ELITO_GIT_REPO}/pub/elito-connman.git;protocol=git"

EXTRA_OEMAKE = "prefix=${prefix} bindir=${bindir}"

S = "${WORKDIR}/git"
