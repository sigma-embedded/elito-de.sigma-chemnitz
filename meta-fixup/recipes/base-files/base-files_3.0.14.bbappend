FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

do_install_append() {
	localstatedir='${localstatedir}'
	rm -f ${D}${sysconfdir}/fstab
}

hostname = ""
BASEFILESISSUEINSTALL = ""
