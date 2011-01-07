include dietlibc.inc

PROVIDES = "dietlibc"
TARGET_CC := "${CC}"

inherit cross

do_compile () {
    oe_runmake all ARCH="${BUILD_ARCH}"  \
        CC="${BUILD_CC}" CFLAGS="${BUILD_CFLAGS}" LDFLAGS="${BUILD_LDFLAGS}" \
        WHAT='$(OBJDIR)/diet $(OBJDIR)/diet-i'

    oe_runmake all profiling ARCH="${TARGET_ARCH}" \
        CC="${TARGET_CC}" CFLAGS="${TARGET_CFLAGS} -Os" LDFLAGS="${TARGET_LDFLAGS}"
}

do_install () {
    install -d -m 0755 ${D}${libdir}/dietlibc/lib-${DIETLIBC_TARGET_ARCH}
    install -d -m 0755 ${D}${libdir}/dietlibc/include

    install -D -p -m 0755 bin-${DIETLIBC_BUILD_ARCH}/diet-i ${D}${bindir}/diet

    for i in `find include -name \*.h`; do
	install -p -D -m 0644 $i ${D}${libdir}/dietlibc/$i
    done

    cd bin-${DIETLIBC_TARGET_ARCH}
    install -p -m 0644 \
        start.o libm.a libpthread.a librpc.a liblatin1.a libcompat.a libcrypt.a \
        pstart.o libgmon.a \
        ${D}${libdir}/dietlibc/lib-${DIETLIBC_TARGET_ARCH}/

    install -D -p -m 0644 dietlibc.a ${D}${libdir}/dietlibc/lib-${DIETLIBC_TARGET_ARCH}/libc.a
}
