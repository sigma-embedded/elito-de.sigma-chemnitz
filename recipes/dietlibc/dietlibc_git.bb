SECTION = "libs"
DESCRIPTION = "The diet libc is a libc that is optimized for small size. \
It can be used to create small statically linked binaries"
LICENSE = "GPLv2"

SRCREV = "${AUTOREV}"
PV = "0.32+gitr${SRCPV}"
INC_PR = "r0"

SRC_URI = " \
	git://github.com/ensc/dietlibc.git;protocol=git;branch=master \
	file://ccache.patch \
        file://arm-elfinfo.patch"
S = "${WORKDIR}/git"

#otherwise the whole run scripts got broken
do_configure () {
:
}

do_compile () {
    mkdir -p bin-${BUILD_ARCH}
    v=dietlibc-$(head -n 1 CHANGES|sed 's/://')

    ${BUILD_CC} ${BUILD_CFLAGS} -DDIETHOME=\"`pwd`\" \
        -DVERSION=\"$v\" -I include/ diet.c lib/write12.c -o bin-${BUILD_ARCH}/diet

    CFLAGS="$CFLAGS -g3 -O2"
    oe_runmake all ARCH="${TARGET_ARCH}" CC="${CC}" STRIP=: prefix=${STAGING_DIR_TARGET}/lib/dietlibc HOME='\${libdir}/dietlibc' MYARCH:=${BUILD_ARCH}
}

#otherwise the whole run scripts got broken
do_install () {
:
}

do_stage () {
	DIETLIBC_BUILD_ARCH=`echo ${BUILD_ARCH} | sed -e s'/.86/386/'`
	DIETLIBC_TARGET_ARCH=`echo ${TARGET_ARCH} | sed -e s'/.86/386/'`
	rm -rf ${STAGING_DIR_TARGET}/lib/dietlibc || true
	rm ${CROSS_DIR}/bin/diet || true
	install -d ${STAGING_DIR_TARGET}/lib/dietlibc/lib-${DIETLIBC_TARGET_ARCH}
	install -d ${STAGING_DIR_TARGET}/lib/dietlibc/include
        for i in `find include -name \*.h`; do install -m 644 -D $i ${STAGING_DIR_TARGET}/lib/dietlibc/$i; done

        install -m755 bin-${DIETLIBC_BUILD_ARCH}/diet-i ${CROSS_DIR}/bin/diet

	cd bin-${DIETLIBC_TARGET_ARCH}
	install -m 644 start.o libm.a libpthread.a librpc.a \
                       liblatin1.a libcompat.a libcrypt.a \
                       ${STAGING_DIR_TARGET}/lib/dietlibc/lib-${DIETLIBC_TARGET_ARCH}
        install -m 644 dietlibc.a ${STAGING_DIR_TARGET}/lib/dietlibc/lib-${DIETLIBC_TARGET_ARCH}/libc.a
}
