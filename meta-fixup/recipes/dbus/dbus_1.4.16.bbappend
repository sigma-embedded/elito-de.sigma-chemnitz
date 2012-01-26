PR := "${PR}.1"

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI += "\
	file://create-machine-uuid \
	file://machine-id.patch \
"

do_install_append() {
	install -D -p -m 0755 ${WORKDIR}/create-machine-uuid \
		${D}${libexecdir}/create-machine-uuid
	touch ${D}${localstatedir}/lib/dbus/machine-id \
              ${D}${sysconfdir}/machine-id
}

FILES_${PN} += "${sysconfdir}/machine-id"
CONFFILES_${PN} += "${sysconfdir}/machine-id ${localstatedir}/lib/dbus/machine-id"
FILES_${PN}-systemd += "${libexecdir}/create-machine-uuid"
