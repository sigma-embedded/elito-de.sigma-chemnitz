DESCRIPTION = "Tool to create images for the Keith & Koep bootloader"
LICENSE     = "GPLv3"

python __anonymous () {
    elito_skip(d, 'arnoldboot')
}

SRCREV = "${AUTOREV}"
PV = "0.0+gitr${SRCPV}"
PR = "r1"

SRC_URI = "git://www.sigma-chemnitz.de/dl/elito/git/arnoldboot.git;protocol=https"
S = "${WORKDIR}/git"

inherit native
