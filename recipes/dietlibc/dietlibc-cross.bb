include dietlibc.inc

PROVIDES = "dietlibc"
TARGET_CC := "${CC}"

inherit cross

do_compile () {
    oe_runmake all ARCH="${BUILD_ARCH}"  \
        CC="${BUILD_CC}" CFLAGS="${BUILD_CFLAGS}" LDFLAGS="${BUILD_LDFLAGS}" \
        WHAT='$(OBJDIR)/diet $(OBJDIR)/diet-i'

    oe_runmake all profiling ARCH="${TARGET_ARCH}" \
        CC="${TARGET_CC}" CFLAGS="${TARGET_CFLAGS} -fno-omit-frame-pointer -Os -g3" LDFLAGS="${TARGET_LDFLAGS}" \

    oe_runmake -C test test DIET=${S}/bin-${DIETLIBC_BUILD_ARCH}/diet \
        CC="${TARGET_CC}" CFLAGS="${TARGET_CFLAGS} -fno-omit-frame-pointer -g3 -L ${S}/bin-${DIETLIBC_TARGET_ARCH} -Os" \
        LDFLAGS="${TARGET_LDFLAGS}" \
}

do_install () {
    install -d -m 0755 ${D}${target_libdir}/dietlibc/lib-${DIETLIBC_TARGET_ARCH}
    install -d -m 0755 ${D}${target_libdir}/dietlibc/include

    install -D -p -m 0755 bin-${DIETLIBC_BUILD_ARCH}/diet-i ${D}${bindir}/diet

    for i in `find include -name \*.h`; do
	install -p -D -m 0644 $i ${D}${target_libdir}/dietlibc/$i
    done

    cd bin-${DIETLIBC_TARGET_ARCH}
    install -p -m 0644 \
        start.o libm.a libpthread.a librpc.a liblatin1.a libcompat.a libcrypt.a \
        pstart.o libgmon.a \
        ${D}${target_libdir}/dietlibc/lib-${DIETLIBC_TARGET_ARCH}/

    install -D -p -m 0644 dietlibc.a ${D}${target_libdir}/dietlibc/lib-${DIETLIBC_TARGET_ARCH}/libc.a
}
