require ffmpeg.inc

LICENSE = "LGPLv2.1+"
DEPENDS += "schroedinger libgsm"
PR = "${INC_PR}.2"

SRC_URI = "http://ffmpeg.org/releases/ffmpeg-${PV}.tar.bz2 \
  file://sha1.h"

EXTRA_FFCONF_armv7a = "--cpu=cortex-a8"
EXTRA_FFCONF_mipsel = "--arch=mips"

EXTRA_OECONF = " \
        --arch=${TARGET_ARCH} \
        --cross-prefix=${TARGET_PREFIX} \
        --disable-stripping \
        --enable-cross-compile \
        --enable-libgsm \
        --enable-libmp3lame \
        --enable-libschroedinger \
        --enable-libtheora  \
        --enable-libvorbis \
        --enable-pthreads \
        --enable-shared \
        --enable-swscale \
        --extra-cflags="${TARGET_CFLAGS} ${HOST_CC_ARCH}${TOOLCHAIN_OPTIONS}" \
        --extra-ldflags="${TARGET_LDFLAGS}" \
        --sysroot="${STAGING_DIR_TARGET}" \
        --prefix=${prefix}/ \
        --target-os=linux \
        ${EXTRA_FFCONF} \
"

do_configure() {
        ./configure ${EXTRA_OECONF}
}

do_install_append() {
        install -p -m 0644 ${WORKDIR}/sha1.h ${D}${includedir}/libavutil/

        rm -rf ${D}${includedir}/ffmpeg
        mkdir -p -m 0755 ${D}${includedir}/ffmpeg

        cd ${D}${includedir}/ffmpeg
        for lib in ${FFMPEG_LIBS}; do
                ln -s ../$lib/*.h '.' || true
        done
        cd -
}

FULL_OPTIMIZATION_armv7a = "-fexpensive-optimizations  -ftree-vectorize -fomit-frame-pointer -O4 -ffast-math"
BUILD_OPTIMIZATION = "${FULL_OPTIMIZATION}"
