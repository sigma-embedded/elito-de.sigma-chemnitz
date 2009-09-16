DEPENDS		= "zlib lzo e2fsprogs-libs"

require mtd-utils.inc
PR		=  "${INC_PR}.0"

do_stage () {
	install -d ${STAGING_INCDIR}/mtd
	for f in ${S}/include/mtd/*.h; do
		install -m 0644 $f ${STAGING_INCDIR}/mtd/
	done
}

do_install () {
	oe_runmake DESTDIR=${D} install

	rm ${D}${sbindir}/ubicrc32.pl
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
