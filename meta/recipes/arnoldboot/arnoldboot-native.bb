DESCRIPTION = "Tool to create images for the Keith & Koep bootloader"
LICENSE     = "GPLv3"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=2c12447f794c304d9cd353f87a432c9e"

_elito_skip := "${@elito_skip(d, 'arnoldboot')}"

SRCREV = "${AUTOREV}"
PV = "0.0+gitr${SRCPV}"
PR = "r1"

SRC_URI = "${ELITO_GIT_REPO}/pub/arnoldboot.git;protocol=git"
S = "${WORKDIR}/git"

inherit native
