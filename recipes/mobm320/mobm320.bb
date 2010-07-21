PV                = "0.3.10"
PR                = "r1"

MOBM320_PATCHES  ?= ""
SRC_URI           = "${ELITO_MIRROR}/elito-${PN}-${PV}.tar.bz2"

SRC_URI[md5sum] = "59ce4db1a65a286ad935fa6eeeaa937c"
SRC_URI[sha256sum] = "f80ed690dc03772b484cf5077b356f18a97cd9b15715de151253a241ddee304a"

include mobm320.inc
