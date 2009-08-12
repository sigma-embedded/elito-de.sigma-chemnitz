DEPENDS		= "zlib-native lzo-native e2fsprogs-libs-native"

GIT_COMMIT	?= ${GIT_COMMIT_pn-mtd-utils}

inherit native
require mtd-utils.inc

PR		=  "${INC_PR}.0"
SRC_URI		+= "file://no-man-install.patch;patch=1"

do_stage () {
        install -d ${STAGING_INCDIR}/mtd
        for f in ${S}/include/mtd/*.h; do
                install -m 0644 $f ${STAGING_INCDIR}/mtd/
        done

	oe_runmake install SBINDIR=${sbindir} DESTDIR=
}
