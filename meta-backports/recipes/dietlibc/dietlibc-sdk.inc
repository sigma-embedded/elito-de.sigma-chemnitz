require dietlibc-common.inc
inherit nativesdk

CFLAGS += "-fno-exceptions -std=gnu99 -Os -Wall -Wmissing-prototypes"
_target_test_cflags = "${CFLAGS} -L ${S}/bin-${DIETLIBC_TARGET_ARCH} \
  -fno-builtin"

do_compile () {
    ln -sf dietfeatures-build.h dietfeatures.h
    oe_runmake all ARCH="${DIETLIBC_BUILD_ARCH}"  \
        CC="${BUILD_CC}" CFLAGS="${BUILD_CFLAGS}" LDFLAGS="${BUILD_LDFLAGS}" \
        WHAT='$(OBJDIR)/diet-i $(OBJDIR)/diet'

    ln -sf dietfeatures-target.h dietfeatures.h
    oe_runmake all profiling ARCH="${TARGET_ARCH}" \
        CC="${CC}" CFLAGS="${CFLAGS}" LDFLAGS="${LDFLAGS}"
}

do_install () {
    dietlibc_install_dev
    install -D -p -m 0755 bin-${DIETLIBC_BUILD_ARCH}/diet-i ${D}${bindir}/diet
}
