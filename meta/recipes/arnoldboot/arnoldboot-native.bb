DESCRIPTION = "Tool to create images for the Keith & Koep bootloader"
LICENSE     = "GPLv3"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"

_elito_skip := "${@elito_skip(d, 'arnoldboot')}"

SRCREV = "b3a30f9b4b791a94ea8e0e6752a3baa2f4fdfb3e"
_pv = "0.0"
PV = "${_pv}+gitr${SRCPV}"
PKGV = "${_pv}+gitr${GITPKGV}"

SRC_URI = "${@elito_uri('${ELITO_GIT_REPO}/pub/arnoldboot.git', d)};protocol=git"
S = "${WORKDIR}/git"

inherit native gitpkgv autotools-brokensep

EXTRA_OEMAKE += "prefix=${prefix}"
