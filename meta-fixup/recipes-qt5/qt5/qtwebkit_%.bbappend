require fixup-pkgconfig.inc

do_install_append() {
        sed -i \
            -e 's!-L${B}/[^[[:space:]]]*!!g' \
            ${D}${libdir}/pkgconfig/*.pc
}

