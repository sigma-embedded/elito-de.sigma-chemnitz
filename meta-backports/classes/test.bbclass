#
# Tasks specific to QA testing of packages
#
# For native and cross packages we want to test
# immediately for other packages we want to package
# the tests including a test
#

do_test () {
    :
}
addtask do_test after do_compile before do_populate_sysroot
