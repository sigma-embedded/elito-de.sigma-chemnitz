do_install_append() {
	# check that fixup is still needed
	grep ${STAGING_DIR_HOST} ${D}${libdir}/pkgconfig/*.pc

	sed -i \
            -e 's!-L${STAGING_DIR_HOST}${libdir}!-L${'libdir'}!g' \
            ${D}${libdir}/pkgconfig/*.pc
}
