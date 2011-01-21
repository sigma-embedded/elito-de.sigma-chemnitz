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

PREFERRED_VERSION_linux-libc-headers     ?= "${MACHINE_KERNEL_VERSION}"

PREFERRED_VERSION_glibc                  ?= "2.10.1"
PREFERRED_VERSION_glibc-initial          ?= "${PREFERRED_VERSION_glibc}"

PREFERRED_VERSION_eglibc                 ?= "2.12"
PREFERRED_VERSION_eglibc-initial         ?= "${PREFERRED_VERSION_eglibc}"

PREFERRED_VERSION_uclibc                 ?= "0.9.31"
PREFERRED_VERSION_uclibc-initial         ?= "${PREFERRED_VERSION_uclibc}"

LIBTOOL_HAS_SYSROOT                      ?= "yes"
PREFERRED_VERSION_libtool                ?= "2.4"
PREFERRED_VERSION_libtool-native         ?= "${PREFERRED_VERSION_libtool}"
PREFERRED_VERSION_libtool-cross          ?= "${PREFERRED_VERSION_libtool}"
PREFERRED_VERSION_libtool-nativesdk      ?= "${PREFERRED_VERSION_libtool}"
PREFERRED_VERSION_libtool-sdk            ?= "${PREFERRED_VERSION_libtool}"

SRCREV_pn-dietlibc         ?= "749ea37e7793f58be8f0131b82d1affd249de244"
