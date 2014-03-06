do_unpackextra() {
    ${@base_contains('PROJECT_FEATURES', 'select-touch', ': > ${S}/pointercal', ':', d)}
}
addtask unpackextra after do_unpack before do_configure
