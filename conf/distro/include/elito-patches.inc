# --*- python -*--
SRC_URI_append_pn-ipkg-utils-native = " \
        file://${ELITO_TOPDIR}/files/ipkg-utils/license.patch;apply=yes"
SRC_URI_append_pn-ipkg-utils        = "\
        file://${ELITO_TOPDIR}/files/ipkg-utils/license.patch;apply=yes"

SRC_URI_append_pn-opkg-native = " \
        file://${ELITO_TOPDIR}/files/opkg/license-info.patch;apply=yes"
SRC_URI_append_pn-opkg        = " \
        file://${ELITO_TOPDIR}/files/opkg/license-info.patch;apply=yes"

SRC_URI_append_pn-busybox     = " \
	file://elito-directory.patch;apply=yes \
        file://cvt-color.patch;apply=yes \
        file://busybox-mbus-env.patch;apply=yes \
        file://ifup-upstart.patch;apply=yes"

# Due to the elito-directory.patch above
ALTERNATIVE_LINK_pn-iproute2 = "${base_sbindir}/ip"

SRC_URI_append_pn-base-passwd = " \
        file://elito-passwd.patch;apply=yes \
        ${PROJECT_PASSWD_PATCH}"

SRC_URI_append_pn-ifupdown = " \
	file://upstart.patch;apply=yes"

SRC_URI_append_pn-bootchart-lite = " \
	file://elito.patch;apply=yes"
