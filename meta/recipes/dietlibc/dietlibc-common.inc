DESCRIPTION = "The diet libc is a libc that is optimized for small size. \
It can be used to create small statically linked binaries"
SECTION = "libs"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=393a5ca445f6965873eca0259a17f833"

EXTRA_OEMAKE = "\
        STRIP=: \
        prefix=${pkglibdir} \
        HOME=\"`pwd`\" \
        MYARCH:='${DIETLIBC_BUILD_ARCH}' \
"

PACKAGES += "${PN}-headers"
RDEPENDS_${PN}-dev += "${PN}-headers (= ${EXTENDPKGV})"

FILES_${PN} = "${bindir}/*"
FILES_${PN}-headers = "${pkglibdir}/include"
FILES_${PN}-dev = "${pkgarchdir}/*"
FILES_${PN}-dbg += "${pkgarchdir}/.debug"

INSANE_SKIP_${PN}-dev += "staticdev"

def dietlibc_arch_translate(arch):
    if arch == 'powerpc':
        return 'ppc';
    elif arch == 'powerpc64':
        return 'ppc64'
    else:
        return re.sub('i[0-9]86', 'i386', arch)

DIETLIBC_BUILD_ARCH[vardeps] += "dietlibc_arch_translate BUILD_ARCH"
DIETLIBC_TARGET_ARCH[vardeps] += "dietlibc_arch_translate TARGET_ARCH"

DIETLIBC_BUILD_ARCH="${@dietlibc_arch_translate(d.getVar('BUILD_ARCH', True))}"
DIETLIBC_TARGET_ARCH="${@dietlibc_arch_translate(d.getVar('TARGET_ARCH', True))}"

export DIETLIBC_BUILD_ARCH
export DIETLIBC_TARGET_ARCH

inherit qemu
export QEMU = "${@qemu_target_binary(d)}"

pkglibdir = "${libdir}/dietlibc"
pkgarchdir = "${pkglibdir}/lib-${DIETLIBC_TARGET_ARCH}"

do_configure () {
    test -e dietfeatures-orig.h || mv dietfeatures.h dietfeatures-orig.h

    dietlibc_fix_features "${DIETLIBC_BUILD_ARCH}" dietfeatures-build.h
    dietlibc_fix_features "${DIETLIBC_TARGET_ARCH}" dietfeatures-target.h

    test -e test/runtests-X.sh || \
        install -p -m 0755 ${WORKDIR}/runtests-X.sh test/runtests-X.sh
}

dietlibc_fix_features () {
    arch=$1
    dst=$2

    # LARGEFILE_BACKCOMPAT is some linux 2.2 thing, and malloc(0) ==
    # NULL is nasty and uncommon
    set -- \
	-e '/#define \(WANT_LARGEFILE_BACKCOMPAT\|WANT_VALGRIND_SUPPORT\)/d' \
	-e 's!^\(/\*\)\?[[:space:]]*\(#define[[:space:]]\+WANT_MALLOC_ZERO\)\([[:space:]]*\*/\)!\2!g'

    case $arch in
    (x86_64|ppc|ppc64)
    ;;

    (*)
    set -- \
        -e '/#define WANT_DYN_PAGESIZE/{c\'     \
        -e '#define WANT_ELFINFO'               \
        -e '}'
    esac

    sed "$@" dietfeatures-orig.h > "$dst"
}

dietlibc_install_dev () {
    install -d -m 0755 ${D}${pkgarchdir}
    install -d -m 0755 ${D}${pkglibdir}/include

    for i in `find include -name \*.h`; do
        install -D -p -m 0644 $i ${D}${pkglibdir}/$i
    done

    cd bin-${DIETLIBC_TARGET_ARCH}
    install -p -m 0644 \
        start.o libm.a libpthread.a librpc.a liblatin1.a libcompat.a libcrypt.a \
        pstart.o libgmon.a dyn_start.o dyn_stop.o \
        ${D}${pkgarchdir}/

    # extra step because 'dietlibc.a' must be renamed to 'libc.a'
    install -D -p -m 0644 dietlibc.a ${D}${pkgarchdir}/libc.a
    cd -
}
