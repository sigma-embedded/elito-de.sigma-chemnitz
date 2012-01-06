do_install_append() {
	install -d -m 0755 ${D}${localstatedir}/lib/connman
}
