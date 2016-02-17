_elito_skip := "${@elito_skip(d, 'barebox')}"

DESCRIPTION = "Barebox (formerly U-Boot v2) bootloader mkimage tool"
LICENSE = "GPLv2"
PROVIDES = "virtual/u-boot-mkimage"
LIC_FILES_CHKSUM = "file://COPYING;md5=057bf9e50e1ca857d0eb97bfe4ba8e5d"

DEFAULT_PREFERENCE = "99"

_repo = "barebox"
_pv = "0.2011.10"

include u-boot-common.inc

do_compile() {
	oe_runmake -e -C scripts mkimage
}

do_install () {
	install -D -p -m 0755 scripts/mkimage ${D}${bindir}/uboot-mkimage
	ln -sf uboot-mkimage ${D}${bindir}/mkimage
}

BBCLASSEXTEND = "native nativesdk"
