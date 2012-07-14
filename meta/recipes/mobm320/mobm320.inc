require mobm320-common.inc
_elito_skip := "${@elito_skip(d, 'mobm320')}"

PROVIDES          = "virtual/bootloader-ibl"
DEPENDS           = "linux-libc-headers mtd-utils mobm320-native virtual/kernel"

PACKAGES         += "${PN}-tools"
FILES_${PN}       = "/boot/*.img"
FILES_${PN}-tools = "/sbin/create-boot-env"
FILES_${PN}-dbg  += "${datadir}/elito-mobm320/${MOBM320_IMAGE}.elf"

MOBM320_ARGS_prepend = "IMAGES='${MOBM320_IMAGE}' IMAGE_KIND='img elf bin' "

CFLAGS += "-I."

do_configure() {
	if test x"${MOBM320_IMAGE}" = x; then
		oefatal "MOBM320_IMAGE not set"
	fi

	ln -s ${STAGING_KERNEL_DIR}/drivers/mtd/ubi mtd
}

do_compile() {
	oe_runmake ${MOBM320_ARGS} _host_all= bin_PROGRAMS=
}

do_install() {
	set -x
	oe_runmake  ${MOBM320_ARGS} \
            PREFIX=${prefix} DESTDIR=${D} install	\
            _host_all= _host_programs= bin_PROGRAMS=

	install -D -p -m 0755 bin/create-env ${D}/sbin/create-boot-env

        install -d -m 0755 ${D}/boot
	mv ${D}${datadir}/elito-mobm320/${MOBM320_IMAGE}.img ${D}/boot/
	rm -f ${D}${datadir}/elito-mobm320/*.img
	rm -f ${D}${datadir}/elito-mobm320/*.bin
}

do_deploy() {
	cd ${D}/boot

	rm -f ${DEPLOY_DIR_IMAGE}/${MOBM320_IMAGE_BASE_NAME}.img
	install -D -p -m 0644 ${MOBM320_IMAGE}.img ${DEPLOY_DIR_IMAGE}/${MOBM320_IMAGE_BASE_NAME}.img

	rm -f ${DEPLOY_DIR_IMAGE}/${MOBM320_IMAGE_SYMLINK_NAME}.img
	ln -s ${MOBM320_IMAGE_BASE_NAME}.img ${DEPLOY_DIR_IMAGE}/${MOBM320_IMAGE_SYMLINK_NAME}.img
}

do_deploy[dirs] = "${S}"
addtask deploy before do_package after do_install
