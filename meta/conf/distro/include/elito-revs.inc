# --*- python -*--
GCCVERSION = "4.6%"
GCCVERSION = "${@elito_base_switch(d, 'TARGET_CPU',   \
                'iwmmxt', '4.3.4', \
                '4.6%')}"

LINUXLIBCVERSION = "${MACHINE_KERNEL_VERSION}"
BINUVERSION = "2.22%"
EGLIBCVERSION = "2.15"

PREFERRED_VERSION_uclibc                 ?= "git"
PREFERRED_VERSION_uclibc-initial         ?= "${PREFERRED_VERSION_uclibc}"

# can not use ?= here because bitbake.conf assigns it already
LIBTOOL_HAS_SYSROOT                       = "yes"
PREFERRED_VERSION_libtool                ?= "2.4.2"
PREFERRED_VERSION_libtool-native         ?= "${PREFERRED_VERSION_libtool}"
PREFERRED_VERSION_libtool-cross          ?= "${PREFERRED_VERSION_libtool}"
PREFERRED_VERSION_libtool-nativesdk      ?= "${PREFERRED_VERSION_libtool}"
PREFERRED_VERSION_libtool-sdk            ?= "${PREFERRED_VERSION_libtool}"

PREFERRED_VERSION_tslib                   = "1.0+git%"
