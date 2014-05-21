DEPENDS += "elito-devicetree"

bootletspath = "${STAGING_DIR_TARGET}${libdir}/imx-bootlets"

ELFTOSB_FLAGS = "\
  -z -f '${FREESCALE_SBARCH}' \
  -p '${bootletspath}' \
  -c '${bootletspath}/uboot_ivt.bd' \
"

ELFTOSB_EXTRA_SOURCES ?= ""

do_builddeploy() {
	freescale-elftosb ${ELFTOSB_FLAGS} -o barebox.sb barebox ${ELFTOSB_EXTRA_SOURCES}
}

do_deploy_append() {
        sb_image=${uboot_image%%.bin}.sb
        sb_symlink=${UBOOT_SYMLINK}
        sb_symlink=${sb_symlink%%.bin}.sb

	install -p -m 0644 ${S}/barebox.sb ${DEPLOYDIR}/$sb_image

	cd ${DEPLOYDIR}
	rm -f ${sb_symlink}
	ln -sf ${sb_image} ${sb_symlink}
        cd -
}
