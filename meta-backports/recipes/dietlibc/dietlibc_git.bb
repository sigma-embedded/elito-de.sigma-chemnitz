require dietlibc-git.inc
require dietlibc.inc

inherit ptest

PTEST_ENABLED_elito = "1"
RDEPENDS_${PN}-ptest += "bash make"

do_compile_ptest() {
    dietlibc_do_full_test
    dietlibc_do_run_tests
}

do_install_ptest() {
    install -d -m 0755 ${D}${PTEST_PATH}
    tar cf - -C test/ . --owner=root --group=root --mode=a+rX,go-w \
        --exclude='*.[ch]' --exclude '*.core' \
        --exclude='.gitignore' --exclude='.cvsignore' \
        | tar xf - -C ${D}${PTEST_PATH}/
}
