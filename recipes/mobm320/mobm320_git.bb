require mobm320.bb

S          = "${WORKDIR}/git"

SRCREV     = "${AUTOREV}"
COMPONENT  = "mobm320"
SRCURI_SPEC_append = ";branch=bgp210"
inherit elito-develcomp
