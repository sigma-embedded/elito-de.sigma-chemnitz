_elito_skip := "${@elito_skip(d, 'u-boot')}"

DESCRIPTION = "U-Boot bootloader mkimage tool"
LICENSE = "GPLv2"
PROVIDES = "virtual/u-boot-mkimage"

PR = "${INCPR}.0"
DEFAULT_PREFERENCE = "99"

include u-boot-common.inc

_make = "${MAKE} -e -f '${TMPDIR}/Makefile.develcomp' \
	CFG=u-boot _secwrap= \
	STRIP=: BUILD_STRIP=: \
	CFG_NONDEVEL=1 \
	CFG_KERNEL_UART=${UBOOT_CONSOLE} \
	CFG_KERNEL_BAUD=${UBOOT_BAUD}"

do_configure() {
	cat << EOF >>config.mk
HOSTCFLAGS += \$(BUILD_CFLAGS)
HOSTCPPFLAGS += \$(BUILD_CPPFLAGS)
HOSTLDFLAGS += \$(BUILD_LDFLAGS)

unexport HOSTCFLAGS HOSTCPPFLAGS
EOF

	${_make} ${UBOOT_MACHINE}
}


do_compile() {
        ${_make} __call T=tools BIN_FILES-y='mkimage$(SFX)' \
}

do_install () {
	install -D -p -m 0755 tools/mkimage ${D}${bindir}/uboot-mkimage
	ln -sf uboot-mkimage ${D}${bindir}/mkimage
}

NATIVE_INSTALL_WORKS = "1"
BBCLASSEXTEND = "native sdk"
