# --*- python -*--
SRC_URI_append_pn-ipkg-utils-native = " \
	file://${ELITO_TOPDIR}/files/ipkg-utils/license.patch;patch=0"
SRC_URI_append_pn-ipkg-utils        = "\
	file://${ELITO_TOPDIR}/files/ipkg-utils/license.patch;patch=0"

SRC_URI_append_pn-opkg-native = " \
	file://${ELITO_TOPDIR}/files/opkg/license-info.patch;patch=0"
SRC_URI_append_pn-opkg        = " \
	file://${ELITO_TOPDIR}/files/opkg/license-info.patch;patch=0"

SRC_URI_append_pn-busybox     = " \
	file://${ELITO_TOPDIR}/files/busybox/cvt-color.patch;patch=1"
