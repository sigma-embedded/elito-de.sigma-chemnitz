PV                = "0.3.6"
PR                = "r1"

MOBM320_PATCHES  ?= ""
SRC_URI           = "${ELITO_MIRROR}/elito-${PN}-${PV}.tar.bz2"

SRC_URI[md5sum] = "646968ca76caed3ed16bdbf2f3a9728b"
SRC_URI[sha256sum] = "ab042cad840b5ff0a05464cfdb3bea1952105416321544bd8ff818555d749a52"

include mobm320.inc
