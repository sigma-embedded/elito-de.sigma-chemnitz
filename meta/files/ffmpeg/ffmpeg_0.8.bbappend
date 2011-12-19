EXTRA_FFCONF_append = " \
	--enable-gpl \
	--enable-nonfree \
	--enable-postproc \
	--enable-x11grab \
	--enable-libfaac \
	--enable-libvpx \
	--enable-libfreetype \
	--enable-libgsm \
	--disable-libx264 \
	--enable-libtheora \
	--enable-libopenjpeg \
	--enable-libspeex \
	pkg_config=pkg-config \
"

ARCH_DEPENDS = ""
ARCH_DEPENDS_x86-64 = "yasm-native"

DEPENDS += "libvpx freetype libgsm openjpeg speex libtheora ${ARCH_DEPENDS}"

do_configure_prepend_i686() {
    export cmov=yes
    export fast_cmov=yes
}
