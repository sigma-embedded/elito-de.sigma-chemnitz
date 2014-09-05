FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

do_install_append() {
	localstatedir='${localstatedir}'
	rmdir ${D}/tmp
	rm -f ${D}${sysconfdir}/fstab
	ln -s "${localstatedir#/}/tmp" ${D}/tmp
}

hostname = ""
BASEFILESISSUEINSTALL = ""
