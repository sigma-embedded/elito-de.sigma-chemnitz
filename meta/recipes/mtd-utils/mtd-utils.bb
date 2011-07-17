DESCRIPTION	= "Tools for managing memory technology devices."
SECTION		= "base"
HOMEPAGE	= "http://www.linux-mtd.infradead.org/"
LICENSE		= "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3"

_pv		= "1.4.1"
PV		= "${_pv}+gitr${SRCPV}"
PKGV		= "${_pv}+git${GITPKGV}"
PR		= "r0"

DEFAULT_PREFERENCE = "99"

inherit gitpkgv

SRCREV  = "3c3674a6e1d3f59554b0ff68ca59be2fd4134e0c"
SRC_URI = "\
        git://git.infradead.org/mtd-utils.git;protocol=git	\
	file://no-man-install.patch \
"
S = "${WORKDIR}/git"

DEPENDS = "zlib lzo2 util-linux-ng"
EXTRA_OEMAKE  = "'CC=${CC}' 'CFLAGS=${CFLAGS} -I${S}/include' WITHOUT_XATTR=1"
PARALLEL_MAKE = ""
NATIVE_INSTALL_WORKS = "1"
BBCLASSEXTEND = "native"

do_install () {
	oe_runmake DESTDIR=${D} SBINDIR=${sbindir} install
        install -d -m 0755 ${D}${includedir}/mtd
        install -p -m 0644 include/mtd/*.h ${D}${includedir}/mtd/

        if "${@['false','true']['${PN}' == 'mtd-utils']}"; then
		rm ${D}${sbindir}/mkpfi
		rm ${D}${sbindir}/ubicrc32.pl
        fi
}

PACKAGES =+ "mkfs-jffs mkfs-jffs2 mkfs-ubifs \
	${PN}-ubi-core ${PN}-ubi-tools ${PN}-ubi-build	\
	${PN}-flash-tools ${PN}-ftl-tools		\
	${PN}-jffs2-build ${PN}-nand-tools"

FILES_${PN}      = ""
FILES_mkfs-jffs  = "${sbindir}/mkfs.jffs"
FILES_mkfs-jffs2 = "${sbindir}/mkfs.jffs2"
FILES_mkfs-ubifs = "${sbindir}/mkfs.ubifs"

FILES_${PN}-jffs2-build = "${sbindir}/sumtool ${sbindir}/jffs2dump"
FILES_${PN}-nand-tools = " \
	${sbindir}/bin2nand ${sbindir}/nand2bin		\
	${sbindir}/nanddump ${sbindir}/nandwrite	\
	${sbindir}/nandtest"

FILES_${PN}-flash-tools	= "\
	${sbindir}/flashcp ${sbindir}/flash_info	\
	${sbindir}/flash_lock ${sbindir}/flash_unlock	\
	${sbindir}/flash_erase ${sbindir}/flash_eraseall	\
	${sbindir}/recv_image ${sbindir}/serve_image"
FILES_${PN}-ftl-tools = "${sbindir}/ftl_check ${sbindir}/ftl_format"

FILES_${PN}-ubi-core   = "${sbindir}/ubiattach ${sbindir}/ubidetach"
RRECOMMENDS_${PN}-ubi-core = "${PN}-ubi-core"
FILES_${PN}-ubi-tools  = "\
	${sbindir}/ubiformat ${sbindir}/ubimirror	\
	${sbindir}/ubimkvol ${sbindir}/ubirmvol         \
	${sbindir}/ubiupdatevol ${sbindir}/ubinfo"
FILES_${PN}-ubi-build  = "${sbindir}/ubigen ${sbindir}/ubinize \
	${sbindir}/unubi ${sbindir}/ubicrc32"
