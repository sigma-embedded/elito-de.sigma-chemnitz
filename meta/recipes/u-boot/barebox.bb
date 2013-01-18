_elito_skip := "${@elito_skip(d, 'barebox')}"

DESCRIPTION = "The barebox (formerly U-Boot v2) bootloader"
PRIORITY = "optional"
LICENSE = "GPLv2"
PROVIDES = "virtual/bootloader"
LIC_FILES_CHKSUM = "file://COPYING;md5=057bf9e50e1ca857d0eb97bfe4ba8e5d"

PR = "${INCPR}.0"
DEFAULT_PREFERENCE = "99"

DEPENDS += "kernel-makefile"
EXTRA_OEMAKE_prepend = "-f ${TMPDIR}/Makefile.kernel _secwrap= V=1 "

PACKAGES         = "${PN}-dbg ${PN}-bin ${PN}"

FILES_${PN}      = "/boot/u-boot"
FILES_${PN}-dbg += "/boot/.debug"
FILES_${PN}-bin  = "/boot/u-boot.bin"

include u-boot-common.inc
inherit deploy

do_configure() {
    oe_runmake "${UBOOT_MACHINE}"
}

bootletspath = "${STAGING_DIR_TARGET}${libdir}/imx-bootlets"

ELFTOSB_FLAGS = "\
  -z -f '${FREESCALE_SBARCH}' \
  -p '${bootletspath}' \
  -c '${bootletspath}/uboot_ivt.bd' \
"

do_builddeploy_img-imx-bootlets() {
	freescale-elftosb ${ELFTOSB_FLAGS} -o barebox.sb barebox
}

do_install() {
	install -D -p -m 0644 barebox.bin ${D}/boot/u-boot.bin
	install -D -p -m 0755 barebox     ${D}/boot/u-boot
}

do_deploy () {
	gitrev=`git ls-remote . HEAD | sed '1s/^\(........\).*/\1/p;d'`
	uboot_image="u-boot-${MACHINE}-${PV}-${PR}-${gitrev}.bin"

	install -D -p -m 0644 ${S}/barebox.bin ${DEPLOYDIR}/${uboot_image}

	cd ${DEPLOYDIR}
	rm -f ${UBOOT_SYMLINK}
	ln -sf ${uboot_image} ${UBOOT_SYMLINK}
        cd -
}

do_deploy_append_img-imx-bootlets() {
        sb_image=${uboot_image%%.bin}.sb
        sb_symlink=${UBOOT_SYMLINK}
        sb_symlink=${sb_symlink%%.bin}.sb

	install -p -m 0644 ${S}/barebox.sb ${DEPLOYDIR}/$sb_image

	cd ${DEPLOYDIR}
	rm -f ${sb_symlink}
	ln -sf ${sb_image} ${sb_symlink}
        cd -
}

addtask deploy before do_build after do_compile
addtask builddeploy after do_compile before do_deploy
