require dietlibc-git.inc
require dietlibc.inc

inherit test

do_test() {
    dietlibc_do_full_test
}
