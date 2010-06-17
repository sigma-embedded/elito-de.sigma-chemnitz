# --*- python -*--
_elito-gcc-version                        = "4.5"
_elito-gcc-version_arm                    = \
    "${@elito_base_switch(d, 'TARGET_CPU',   \
                          'iwmmxt', '4.3.4', \
                          '4.5')}"

PREFERRED_VERSION_gcc                    ?= "${_elito-gcc-version}"

PREFERRED_VERSION_gcc-cross              ?= "${PREFERRED_VERSION_gcc}"
PREFERRED_VERSION_gcc-cross-sdk          ?= "${PREFERRED_VERSION_gcc}"
PREFERRED_VERSION_gcc-cross-initial      ?= "${PREFERRED_VERSION_gcc}"
PREFERRED_VERSION_gcc-cross-intermediate ?= "${PREFERRED_VERSION_gcc}"

PREFERRED_VERSION_linux-libc-headers     ?= "2.6.32"

PREFERRED_VERSION_glibc                  ?= "2.10.1"
PREFERRED_VERSION_glibc-initial          ?= "${PREFERRED_VERSION_glibc}"

PREFERRED_VERSION_eglibc                 ?= "2.12"
PREFERRED_VERSION_eglibc-initial         ?= "${PREFERRED_VERSION_eglibc}"

SRCREV_pn-opkg        ?= 533
SRCREV_pn-opkg-native ?= ${SRCREV_pn-opkg}
