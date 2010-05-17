PV		= "0.4.6"
PR		= "r1"

SRC_URI		= "${ELITO_MIRROR}/${PN}-${PV}.tar.bz2;name=tarball"

SRC_URI[tarball.md5sum] = "d3ff76b01b685c43779aaf1abcdd3dab"
SRC_URI[tarball.sha256sum] = "89d320a6e599327401b1a40d6ff14fbee718c806a9f3bd195eebbe9fb31ba795"

require upstart-setup.inc
