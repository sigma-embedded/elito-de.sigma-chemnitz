_elito_skip := "${@elito_skip(d, 'mobm320')}"

DESCRIPTION       = "Initial bootloader for PXA320"
SECTION           = "bootloaders"
PRIORITY          = "optional"
LICENSE           = "GPLv3"
PROVIDES          = "virtual/bootloader-ibl"
PACKAGE_ARCH      = "${MACHINE_ARCH}"

DEPENDS           = "linux-libc-headers mtd-utils mobm320-native"
S                 = "${WORKDIR}/elito-${PN}-${PV}"

PACKAGES         += "${PN}-tools"
FILES_${PN}       = "/boot/*"
FILES_${PN}-tools = "/sbin/create-boot-env"

MOBM320_IMAGE	?=
MOBM320_ARGS	?=

MOBM320_IMAGE_BASE_NAME		?= "mobm320-${PV}-${PR}-${MACHINE}"
MOBM320_IMAGE_SYMLINK_NAME	?= "mobm320-${MACHINE}"

MOBM320_ARGS_prepend = "IMAGES='${MOBM320_IMAGE}' IMAGE_KIND='img elf bin' "


do_configure() {
	if test x"${MOBM320_IMAGE}" = x; then
		oefatal "MOBM320_IMAGE not set"
	fi
}

do_compile() {
	oe_runmake ${MOBM320_ARGS} _host_all= bin_PROGRAMS=
}

do_install() {
	set -x
	oe_runmake  ${MOBM320_ARGS} \
            PREFIX=${prefix} DESTDIR=${D} install	\
            _host_all= _host_programs= bin_PROGRAMS=

	mkdir -p ${D}/boot ${D}/sbin
	install -p -m 0755 bin/create-env ${D}/sbin/create-boot-env

	mv ${D}${datadir}/elito-mobm320/${MOBM320_IMAGE}.img ${D}/boot/
	rm -f ${D}${datadir}/elito-mobm320/*.img
}

do_deploy() {
	install -d ${DEPLOY_DIR_IMAGE}
	cd ${D}/boot

	rm -f ${DEPLOY_DIR_IMAGE}/${MOBM320_IMAGE_BASE_NAME}.img \
              ${DEPLOY_DIR_IMAGE}/${MOBM320_IMAGE_BASE_NAME}.bin \
              ${DEPLOY_DIR_IMAGE}/${MOBM320_IMAGE_BASE_NAME}.elf
	install -p -m 0644 ${MOBM320_IMAGE}.img ${DEPLOY_DIR_IMAGE}/${MOBM320_IMAGE_BASE_NAME}.img
	install -p -m 0644 ${D}${datadir}/elito-mobm320/*.bin ${DEPLOY_DIR_IMAGE}/${MOBM320_IMAGE_BASE_NAME}.bin
	install -p -m 0644 ${D}${datadir}/elito-mobm320/*.elf ${DEPLOY_DIR_IMAGE}/${MOBM320_IMAGE_BASE_NAME}.elf

	rm -f ${DEPLOY_DIR_IMAGE}/${MOBM320_IMAGE_SYMLINK_NAME}.img \
              ${DEPLOY_DIR_IMAGE}/${MOBM320_IMAGE_SYMLINK_NAME}.bin \
              ${DEPLOY_DIR_IMAGE}/${MOBM320_IMAGE_SYMLINK_NAME}.elf
	ln -s ${MOBM320_IMAGE_BASE_NAME}.img ${DEPLOY_DIR_IMAGE}/${MOBM320_IMAGE_SYMLINK_NAME}.img
	ln -s ${MOBM320_IMAGE_BASE_NAME}.bin ${DEPLOY_DIR_IMAGE}/${MOBM320_IMAGE_SYMLINK_NAME}.bin
	ln -s ${MOBM320_IMAGE_BASE_NAME}.elf ${DEPLOY_DIR_IMAGE}/${MOBM320_IMAGE_SYMLINK_NAME}.elf
}

do_deploy[dirs] = "${S}"
addtask deploy before do_package after do_install
