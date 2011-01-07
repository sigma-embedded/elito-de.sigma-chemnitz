_elito_skip := "${@elito_skip(d, 'u-boot')}"

DESCRIPTION = "U-boot bootloader mkimage tool"
LICENSE = "GPLv2"
SECTION = "bootloader"
PACKAGE_ARCH = "${MACHINE_ARCH}"

PV = "${UBOOT_VERSION}"
PR = "r0"

DEFAULT_PREFERENCE = "99"
SRCREV           = "${AUTOREV}"

UBOOT_BRANCH	?= "${KERNEL_BRANCH}"
UBOOT_MACHINE	?= "${MACHINE}_config"
UBOOT_REV       ?= "branch=${UBOOT_VERSION}/${UBOOT_BRANCH}"

SRCURI_SPEC_append	=  ";${UBOOT_REV}"
COMPONENT		=  u-boot

inherit elito-develcomp

S = "${WORKDIR}/git"

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
