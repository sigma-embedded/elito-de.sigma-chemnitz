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

SRCREV_pn-opkg        ?= 554
SRCREV_pn-opkg-native ?= "${SRCREV_pn-opkg}"

SRCREV_pn-mtd-utils        ?= "004760f969175da7392dd02bb4fa14164ff46c1e"
SRCREV_pn-kernel-firmware  ?= "1533edfac39d8a1be35d960d3b2b9516b945ad1c"
SRCREV_pn-iproute2         ?= "02833d1b381f468c8744f786061b36b7a4c2572a"
