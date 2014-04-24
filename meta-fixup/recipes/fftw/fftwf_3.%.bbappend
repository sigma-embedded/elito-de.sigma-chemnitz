do_compile_append() {
    oe_runmake -C tests bench
}

do_install_append() {
    libtool --mode=install install -D -p -m 0755 tests/bench \
	${D}${bindir}/${BPN}-bench
}
