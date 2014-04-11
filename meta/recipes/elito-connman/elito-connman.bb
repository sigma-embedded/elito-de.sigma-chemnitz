SUMMARY = "Sample programs demonstrating setup of connman"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"

SRCREV = "00812bfb87be6275e91376f47127bbb3f7d8a435"

_pv = "0.0.2"
PV = "${_pv}+gitr${SRCPV}"

inherit gitpkgv autotools

DEPENDS = "dbus-glib"
SRC_URI = "${ELITO_GIT_REPO}/pub/elito-connman.git;protocol=git"

EXTRA_OEMAKE = "prefix=${prefix} bindir=${bindir}"

S = "${WORKDIR}/git"
