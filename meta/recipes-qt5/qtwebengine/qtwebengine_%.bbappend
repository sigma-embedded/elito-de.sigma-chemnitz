do_configure_append() {
    cat <<EOF >>Makefile
install:	sub-examples-install_subtargets
EOF
}

do_compile_append() {
	oe_runmake all
}
