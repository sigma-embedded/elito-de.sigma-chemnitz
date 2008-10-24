DESCRIPTION	= "Tools for managing memory technology devices."
SECTION		= "base"
DEPENDS		= "zlib lzo e2fsprogs-libs"
HOMEPAGE	= "http://www.linux-mtd.infradead.org/"
LICENSE		= "GPLv2"
FILE_PR		= "r0"

SRCREV  = "${AUTOREV}"
SRC_URI = "	\
	git://git.infradead.org/mtd-utils.git;protocol=git;branch=master	\
	file:///srv/elito/toolchain/devel/elito-mtd/mtd-utils-1.2.0-flags.patch;patch=1	\
"
S = "${WORKDIR}/git"

EXTRA_OEMAKE = "'CC=${CC}' 'CFLAGS=${CFLAGS} -I${S}/include -DWITHOUT_XATTR'"

do_stage () {
	install -d ${STAGING_INCDIR}/mtd
	for f in ${S}/include/mtd/*.h; do
		install -m 0644 $f ${STAGING_INCDIR}/mtd/
	done
	for binary in ${mtd_utils}; do
		install -m 0755 $binary ${STAGING_BINDIR}
	done
}

mtd_utils = "ftl_format flash_erase flash_eraseall nanddump doc_loadbios \
             mkfs.jffs ftl_check mkfs.jffs2 flash_lock flash_unlock flash_info mtd_debug \
             flashcp nandwrite jffs2dump sumtool"

do_install () {
	oe_runmake DESTDIR=${D}
}

PACKAGES =+ "mkfs-jffs mkfs-jffs2"
FILES_mkfs-jffs  = "${bindir}/mkfs.jffs"
FILES_mkfs-jffs2 = "${bindir}/mkfs.jffs2"

