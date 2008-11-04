DESCRIPTION	 = "Initial bootloader for PXA320"
SECTION		 = "bootloaders"
PRIORITY	 = "optional"
LICENSE		 = "GPLv3"
PROVIDES	 = "virtual/bootloader-ibl"
PACKAGE_ARCH	 = "${MACHINE_ARCH}"
PV		 = "0.3"
PR		 = "r0"

DEPENDS		 = "linux-libc-headers mtd-utils mobm320-native"
S		 = ${WORKDIR}/elito-${PN}-${PV}

SRC_URI = "\
#	${ELITO_MIRROR}/mobm320/${PN}-${PV}.tar.bz2	\
	file:///home/ensc/src/mobm/elito-${PN}-${PV}.tar.bz2	\
"

PACKAGES	 += "${PN}-tools"
FILES_${PN}	  = "/boot/*"
FILES_${PN}-tools = "/sbin/create-boot-env"

MOBM320_IMAGE	?=
MOBM320_ARGS	?=

MOBM320_IMAGE_BASE_NAME		?= "mobm320-${PV}-${PR}-${MACHINE}"
MOBM320_IMAGE_SYMLINK_NAME	?= "mobm320-${MACHINE}.img"


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
	oe_runmake PREFIX=${prefix} DESTDIR=${D} install	\
		_host_all= _host_programs= bin_PROGRAMS=

	mkdir -p ${D}/boot ${D}/sbin
	install -p -m0755 bin/create-env ${D}/sbin/create-boot-env

	mv ${D}${datadir}/elito-mobm320/${MOBM320_IMAGE} ${D}/boot/
	rm -f ${D}${datadir}/elito-mobm320/*.img
}

do_deploy() {
	install -d ${DEPLOY_DIR_IMAGE}
	cd ${D}/boot

	rm -f ${DEPLOY_DIR_IMAGE}/${MOBM320_IMAGE_BASE_NAME}.img
	install -p -m0644 ${MOBM320_IMAGE} ${DEPLOY_DIR_IMAGE}/${MOBM320_IMAGE_BASE_NAME}.img
	rm -f ${DEPLOY_DIR_IMAGE}/${MOBM320_IMAGE_SYMLINK_NAME}
	ln -s ${MOBM320_IMAGE_BASE_NAME}.img ${DEPLOY_DIR_IMAGE}/${MOBM320_IMAGE_SYMLINK_NAME}
}

do_deploy[dirs] = "${S}"
addtask deploy before do_package after do_install
