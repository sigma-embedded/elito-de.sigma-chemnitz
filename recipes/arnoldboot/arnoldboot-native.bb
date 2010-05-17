_elito_skip := "${@elito_skip(d, 'arnoldboot')}"

DESCRIPTION = "Tool to create images for the Keith & Koep bootloader"
LICENSE     = "GPLv3"

SRCREV = "${AUTOREV}"
PV = "0.0+gitr${SRCPV}"
PR = "r1"

SRC_URI = "${ELITO_GIT_REPO}/pub/arnoldboot.git;protocol=git"
S = "${WORKDIR}/git"

inherit native
