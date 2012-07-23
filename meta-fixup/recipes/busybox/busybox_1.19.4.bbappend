FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI += " \
        file://cvt-color.patch \
        file://busybox-mbus-env.patch \
        file://ifup-upstart.patch \
\
	file://test-access.patch \
"

do_install_append() {
    rm -f ${D}${base_libdir}/systemd/system/syslog.service
    ln -s busybox-syslog.service ${D}${base_libdir}/systemd/system/syslog.service
}

FILES_${PN}-syslog-systemd += "${D}${base_libdir}/system/system/syslog.service"
