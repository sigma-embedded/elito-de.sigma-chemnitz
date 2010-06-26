PV                = "0.3.7"
PR                = "r1"

MOBM320_PATCHES  ?= ""
SRC_URI           = "${ELITO_MIRROR}/elito-${PN}-${PV}.tar.bz2"

SRC_URI[md5sum] = "fffd8eaf9c4945bcd7edadb83b5ad513"
SRC_URI[sha256sum] = "b5c5e280c84bf926336d38cd7feadda72712847f57ee4e0371a4af6a273f8661"

include mobm320.inc
