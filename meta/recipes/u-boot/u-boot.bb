_elito_skip := "${@elito_skip(d, 'u-boot')}"

DESCRIPTION = "The U-Boot bootloader"
PRIORITY = "optional"
LICENSE = "GPL"
PROVIDES = "virtual/bootloader"
LIC_FILES_CHKSUM = "file://COPYING;md5=4c6cde5df68eff615d36789dc18edd3b"

PR = "${INCPR}.0"
DEFAULT_PREFERENCE = "99"
DEPENDS += "elito-makefile"

include u-boot-common.inc
inherit deploy

PACKAGES         = "${PN}-dbg ${PN}-bin ${PN}"

FILES_${PN}      = "/boot/u-boot"
FILES_${PN}-dbg += "/boot/.debug"
FILES_${PN}-bin  = "/boot/u-boot.bin"

_make = "${MAKE} -f '${TMPDIR}/Makefile.develcomp' \
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
	${_make} include/autoconf.mk || :
	${_make} clean
}

do_compile() {
	${_make}
}

do_install() {
	install -D -p -m 0644 u-boot.bin ${D}/boot/u-boot.bin
	install -D -p -m 0755 u-boot     ${D}/boot/u-boot
}

do_deploy () {
	set -x
	gitrev=`git ls-remote . HEAD | sed '1s/^\(........\).*/\1/p;d'`
	uboot_image="u-boot-${MACHINE}-${PV}-${PR}-${gitrev}.bin"

	install -d ${DEPLOYDIR}
	install ${S}/u-boot.bin ${DEPLOYDIR}/${uboot_image}

	cd ${DEPLOYDIR}
	rm -f ${UBOOT_SYMLINK}
	ln -sf ${uboot_image} ${UBOOT_SYMLINK}
}
addtask deploy before do_build after do_compile
