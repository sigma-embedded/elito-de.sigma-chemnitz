_elito_skip := "${@elito_skip(d, 'barebox')}"

DESCRIPTION = "Barebox (formerly U-Boot v2) bootloader mkimage tool"
LICENSE = "GPLv2"
PROVIDES = "virtual/u-boot-mkimage"
LIC_FILES_CHKSUM = "file://COPYING;md5=4c6cde5df68eff615d36789dc18edd3b"

PR = "${INCPR}.0"
DEFAULT_PREFERENCE = "99"

include u-boot-common.inc

do_compile() {
	oe_runmake -C scripts mkimage
}

do_install () {
	install -D -p -m 0755 scripts/mkimage ${D}${bindir}/uboot-mkimage
	ln -sf uboot-mkimage ${D}${bindir}/mkimage
}

NATIVE_INSTALL_WORKS = "1"
BBCLASSEXTEND = "native"
