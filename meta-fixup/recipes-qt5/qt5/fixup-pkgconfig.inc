do_install_append() {
	sed -i \
            -e 's!^prefix=${STAGING_DIR_HOST}${prefix}!prefix=${prefix}!g' \
            -e 's!-L${STAGING_DIR_HOST}${libdir}!-L${'libdir'}!g' \
            ${D}${libdir}/pkgconfig/*.pc
}
