require libvpx.inc

PR = "${INC_PR}.0"

SRC_URI += "file://libvpx-configure-support-blank-prefix.patch"

SRC_URI[md5sum] = "bd888cffde8d9c3061c7fd719b0cd4ce"
SRC_URI[sha256sum] = "074583d46955d406cf63c6a8d0d5b0a75c2b98128e6d425ab481f57612e291f0"

CONFIGUREOPTS += " \
        --prefix=${prefix} \
        --libdir=${libdir} \
"
