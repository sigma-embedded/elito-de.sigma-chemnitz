do_install_append() {
    chown -h -R root:root ${D}${datadir}/${P}
}
