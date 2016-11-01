python() {
    orig_cc = d.getVar("CC", False)
    d.setVar("ORIG_CC", orig_cc)
    d.setVar("CC", "${WORKDIR}/gcc")
}

do_create_gcc_wrapper() {
	cat << "EOF" > "${CC}"
#! /bin/sh
exec ${ORIG_CC} "$@"
EOF
	chmod 0755 "${CC}"
}
addtask create_gcc_wrapper before do_configure
