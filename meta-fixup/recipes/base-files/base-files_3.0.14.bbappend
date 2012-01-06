do_install_append() {
	rmdir ${D}/tmp
	ln -s ${localstatedir}/tmp ${D}/tmp
}
