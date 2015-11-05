do_install_append() {
	install -d -m 0750 ${D}/var/lib/bluetooth
}
