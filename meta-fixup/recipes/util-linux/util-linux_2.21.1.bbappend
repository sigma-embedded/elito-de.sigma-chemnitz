EXTRA_OECONF_append_virtclass-native = " --without-ncurses"

do_configure_prepend_virtclass-native() {
	export AUTOPOINT=true
}
