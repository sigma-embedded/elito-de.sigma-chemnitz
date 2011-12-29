PV		= "0.5"
PR		= "r1"

SRC_URI		= "${ELITO_MIRROR}/${PN}-${PV}.tar.bz2"

SRC_URI[md5sum] = "a4589704498395ed6e41af9c8df507f3"
SRC_URI[sha256sum] = "3c58ea305d2ea92f3df00db205c96bf190a60924456a09dc1293870413675aaf"

require upstart-setup.inc
