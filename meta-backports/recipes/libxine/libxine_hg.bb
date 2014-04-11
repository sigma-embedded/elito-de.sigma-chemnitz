require libxine.inc


PPDIR = "2.0"
PV = "1.1.90"

SRCREV = "c91a144240d7"

FILESPATHPKG .= ":libxine-1.1.16"
DEPENDS += "libmpcdec libxinerama libxcb libxvmc"

SRC_URI  = "hg://hg.debian.org;module=hg/xine-lib/xine-lib-1.2;rev=${SRCREV} \
	file://ffmpeg-flac-hostinclude.patch \
	file://ffmpeg08.patch \
        "

S = "${WORKDIR}/hg/xine-lib/xine-lib-1.2"

EXTRA_OECONF += "--disable-vidix --disable-directfb --enable-vdr"

python populate_packages_prepend () {
    bb.data.setVar('PKG_libxine', 'libxine', d)

    plugindir = bb.data.expand('${libdir}/xine/plugins/${PPDIR}', d)
    do_split_packages(d, plugindir, '^xineplug_(.*)\.so$', 'libxine-plugin-%s', 'Xine plugin for %s', extra_depends='' )

    #vidixdir = bb.data.expand('${libdir}/xine/plugins/${PPDIR}/vidix', d)
    #do_split_packages(d, vidixdir, '^(.*)\.so$', 'libxine-plugin-%s', 'Xine plugin for %s', extra_depends='' )

    postdir = bb.data.expand('${libdir}/xine/plugins/${PPDIR}/post', d)
    do_split_packages(d, postdir, '^xineplug_(.*)\.so$', 'libxine-plugin-%s', 'Xine plugin for %s', extra_depends='' )

    fontdir = bb.data.expand('${datadir}/xine-lib/fonts', d)
    do_split_packages(d, fontdir, '^(.*).xinefont.gz$', 'libxine-font-%s', 'Xine font %s', extra_depends='' )
}

FILES_${PN}-dbg =+ "${libdir}/xine/plugins/${PPDIR}/.debug \
                    ${libdir}/xine/plugins/${PPDIR}/post/.debug \
                    ${libdir}/xine/plugins/${PPDIR}/vidix/.debug \
                   "

FILES_${PN}-dev =+ "${libdir}/xine/plugins/${PPDIR}/*.a \
                    ${libdir}/xine/plugins/${PPDIR}/post/*.a \
                    ${libdir}/xine/plugins/${PPDIR}/vidix/*.a \
                   "

do_configure_prepend() {
    autopoint
    export ac_cv_header_ffmpeg_avutil_h=no
}
