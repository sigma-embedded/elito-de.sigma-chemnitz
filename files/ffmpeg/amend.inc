EXTRA_FFCONF_append = " \
	--enable-gpl \
	--enable-nonfree \
	--enable-postproc \
	--enable-x11grab \
	--enable-libfaac \
	--enable-libfaad \
	--enable-libfaadbin \
	--enable-libvpx \
	--enable-libx264 \
	--enable-libtheora \
	--enable-libopenjpeg \
	--enable-libspeex \
"

DEPENDS += "libvpx openjpeg speex"

do_configure_prepend_i686() {
    export cmov=yes
    export fast_cmov=yes
}
