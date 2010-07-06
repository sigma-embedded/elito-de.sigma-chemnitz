DEPENDS		= "zlib-native lzo2-native util-linux-ng-native"

inherit native
require mtd-utils.inc

PR		=  "${INC_PR}.0"
SRC_URI		+= "file://no-man-install.patch"

do_stage () {
        install -d ${STAGING_INCDIR}/mtd
        for f in ${S}/include/mtd/*.h; do
                install -m 0644 $f ${STAGING_INCDIR}/mtd/
        done

	oe_runmake install SBINDIR=${sbindir} DESTDIR=
}
