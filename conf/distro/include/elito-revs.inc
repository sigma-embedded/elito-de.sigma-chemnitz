# --*- python -*--
PREFERRED_VERSION_gcc                    ?= "4.3.4"
PREFERRED_VERSION_gcc-cross              ?= "${PREFERRED_VERSION_gcc}"
PREFERRED_VERSION_gcc-cross-sdk          ?= "${PREFERRED_VERSION_gcc}"
PREFERRED_VERSION_gcc-cross-initial      ?= "${PREFERRED_VERSION_gcc}"
PREFERRED_VERSION_gcc-cross-intermediate ?= "${PREFERRED_VERSION_gcc}"

PREFERRED_VERSION_linux-libc-headers     ?= "2.6.30"

PREFERRED_VERSION_glibc                  ?= "2.10"
PREFERRED_VERSION_glibc-initial          ?= "2.10"

PREFERRED_VERSION_eglibc                 ?= "2.11"
PREFERRED_VERSION_eglibc-initial         ?= "2.11"

SRCREV_pn-opkg        ?= 318
SRCREV_pn-opkg-native ?= ${SRCREV_pn-opkg}
