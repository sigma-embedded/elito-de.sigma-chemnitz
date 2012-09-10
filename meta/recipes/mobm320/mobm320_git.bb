include mobm320.inc

PV = "${_pv}+gitr${SRCPV}"
PKGV = "${_pv}+gitr${GITPKGV}"

MOBM320_BRANCH ?= "${KERNEL_BRANCH}"

#SRCREV  = "e7a58a0d93b125d6937e1c163ebe847ae4ad121d"
SRCREV = "${AUTOREV}"
SRC_URI = "${ELITO_GIT_REPO}/mobm320.git;branch=${MOBM320_BRANCH}"
S = "${WORKDIR}/git"

inherit gitpkgv
