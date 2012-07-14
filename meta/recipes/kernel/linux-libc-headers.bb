SRCREV = "${AUTOREV}"
PV = "${MACHINE_KERNEL_VERSION}"
PR = "r0"

KERNEL_REPO  ?= "${ELITO_GIT_WS}/kernel.git"
_branch       = "${MACHINE_KERNEL_VERSION}/kernel.org"
SRC_URI_elito = "git://${KERNEL_REPO};protocol=file;branch=${_branch}"

DEFAULT_PREFERENCE = "99"

require ${OECORE_TOPDIR}/meta/recipes-kernel/linux-libc-headers/linux-libc-headers.inc

S            = "${WORKDIR}/git"
