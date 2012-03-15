FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

PRINC := "${@int(PRINC) + 2}"

SRC_URI += "file://0001-start-ntp-service-when-interface-is-ready.patch"

INITSCRIPT_PACKAGES = "${PN}-sysv"

do_install_append() {
	install -d -m 0755 ${D}${localstatedir}/lib/connman
}
