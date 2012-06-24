FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

INITSCRIPT_PACKAGES = "${PN}-sysv"

do_install_append() {
	install -d -m 0755 ${D}${localstatedir}/lib/connman
}
