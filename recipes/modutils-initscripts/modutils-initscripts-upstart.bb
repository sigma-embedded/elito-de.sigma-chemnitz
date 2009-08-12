SECTION          = "base"
DESCRIPTION      = "modutils configuration files for upstart"
LICENSE          = "GPL"
SRC_URI          = "file://modutils.sh"
PR               = "r1"

PACKAGES         = "${PN}"
PROVIDES         = "virtual/modutils-initscripts"
RDEPENDS	+= "upstart"

PACKAGE_ARCH     =  "all"
