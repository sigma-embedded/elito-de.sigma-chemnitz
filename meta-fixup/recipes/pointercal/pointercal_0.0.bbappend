do_configure_append() {
    ${@base_contains('PROJECT_FEATURES', 'select-touch', ': > ${S}/pointercal', ':', d)}
}
