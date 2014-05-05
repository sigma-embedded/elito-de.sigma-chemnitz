INITSCRIPT_PACKAGES = "${PN}-sysv"

do_install_append() {
	install -d -m 0755 ${D}${localstatedir}/lib/connman
}

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI += "\
  file://0001-added-noipconfig-option.patch \
  file://no-dhcp-id.patch \
"

DEPENDS += "readline"
