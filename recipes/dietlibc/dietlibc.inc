SECTION = "libs"
DESCRIPTION = "The diet libc is a libc that is optimized for small size. \
It can be used to create small statically linked binaries"
LICENSE = "GPLv2"

SRCREV = "749ea37e7793f58be8f0131b82d1affd249de244"
_pv = "0.32"
PR = "r0"

PV = "${_pv}+gitr${SRCPV}"
PKGV = "${_pv}+gitr${GITPKGV}"

SRC_URI = " \
	git://github.com/ensc/dietlibc.git;protocol=git;branch=master \
	file://ccache.patch \
"
S = "${WORKDIR}/git"

EXTRA_OEMAKE = "\
	STRIP=: \
	prefix=${STAGING_DIR_TARGET}${libdir}/dietlibc \
	HOME=\"`pwd`\" \
	MYARCH:='${BUILD_ARCH}' \
"


inherit gitpkgv

export DIETLIBC_BUILD_ARCH="${@re.sub('[0-9]86', '386','${BUILD_ARCH}')}"
export DIETLIBC_TARGET_ARCH="${@re.sub('[0-9]86', '386','${TARGET_ARCH}')}"

#otherwise the whole run scripts got broken
do_configure () {
    :
}

FILES_${PN}      = "${bindir}/*"
FILES_${PN}-dev += "${libdir}/dietlibc/*"
FILES_${PN}-dbg += "${libdir}/dietlibc/lib-*/.debug"
