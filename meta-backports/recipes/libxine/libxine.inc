DESCRIPTION = "libxine is a versatile multimedia library decoding a lot of common audio and video formats."
SECTION = "libs"
PRIORITY = "optional"
LICENSE = "GPL"
DEPENDS = "ffmpeg zlib libogg libvorbis tremor libmad libmodplug libpng alsa-lib virtual/libiconv virtual/libx11 \
           libxv libxext directfb pulseaudio libtool"
DEPENDS += "flac"
LIC_FILES_CHKSUM = "\
  file://COPYING;md5=751419260aa954499f7abaabaa882bbe \
  file://COPYING.LIB;md5=b29c0845cfd8eb68f25dd58c624c161f \
"

inherit autotools gettext binconfig pkgconfig

SRC_URI = "${SOURCEFORGE_MIRROR}/xine/xine-lib-${PV}.tar.bz2"

S = "${WORKDIR}/xine-lib-${PV}"

SOV = "1.0.7"

EXTRA_OECONF = "--with-zlib-path=${STAGING_EXECPREFIXDIR} \
	--with-vorbis-prefix=${STAGING_EXECPREFIXDIR} \
	--disable-oggtest --with-ogg-prefix=${STAGING_EXECPREFIXDIR} \
	--disable-iconvtest \
        --disable-asf --disable-faad \
        --disable-aalib  --disable-aalibtest \
        --disable-esd    --disable-esdtest \
        --disable-dvdnav --disable-dvdnavtest \
        --disable-vcd \
        --without-arts --without-sdl \
	--disable-altivec --disable-vis --disable-mlib \
	--without-v4l --disable-dxr3 --disable-libfametest\
	--disable-rpath \
	--enable-fb --enable-directfb \
        --with-x --x-includes=${STAGING_INCDIR}/X11 \
        --x-libraries=${STAGING_LIBDIR} \
	"

LIBTOOL = "${HOST_SYS}-libtool"
EXTRA_OEMAKE = "'LIBTOOL=${LIBTOOL}'"

PACKAGES_DYNAMIC = "libxine-plugin-* libxine-font-*"

#python populate_packages_append () {
#    bb.data.setVar('PKG_libxine', 'libxine', d)
#
#    plugindirdbg = bb.data.expand('${libdir}/xine/plugins/${PPDIR}/.debug', d)
#    do_split_packages(d, plugindirdbg, '^xineplug_(.*)\.so$', 'libxine-plugin-%s-dbg', 'Xine plugin for %s debugversion', extra_depends='' )
#
#    postdirdbg = bb.data.expand('${libdir}/xine/plugins/${PPDIR}/post/.debug', d)
#    do_split_packages(d, postdirdbg, '^xineplug_(.*)\.so$', 'libxine-plugin-%s-dbg', 'Xine plugin for %s debugversion', extra_depends='' )
#}