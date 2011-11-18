SRC_URI += " \
	file://elito-directory.patch \
        file://cvt-color.patch \
        file://busybox-mbus-env.patch \
        file://ifup-upstart.patch \
	file://linux30.patch \
\
	file://syslogd.service \
	file://klogd.service \
	file://test-access.patch \
"

PACKAGES =+ "${PN}-systemd"
RDEPENDS_${PN}-systemd += "systemd"
FILES_${PN}-systemd = "${base_libdir}/systemd/system/*"

unitdir = "${base_libdir}/systemd/system"

do_install_append() {
	install -d ${D}${unitdir}/multi-user.target.wants

	for i in syslogd.service klogd.service; do
		install -D -p -m 0644 ${WORKDIR}/$i ${D}${unitdir}/$i
		ln -s ../$i ${D}${unitdir}/multi-user.target.wants/
        done

}
