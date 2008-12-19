DESCRIPTION	 = "Initial bootloader for PXA320"
SECTION		 = "bootloaders"
PRIORITY	 = "optional"
LICENSE		 = "GPLv3"
PACKAGE_ARCH	 = "${MACHINE_ARCH}"
PV		 = "0.3.3"
PR		 = "r0"
SRC_URI		 = "${ELITO_MIRROR}/elito-mobm320-${PV}.tar.bz2"
S		 = ${WORKDIR}/elito-mobm320-${PV}

PACKAGES	 = "${PN}-dbg ${PN}"
FILES_${PN}	 = ${bindir}/mobm320-create-env

inherit native

do_configure() {
	: > src/Makefile-files
}

do_compile() {
	oe_runmake ${MOBM320_ARGS}
}

do_install() {
	oe_runmake PREFIX=${prefix} DESTDIR=${D} install

	cd ${D}${datadir}/elito-mobm320
	for i in *.img; do
		test x"$i" = x"${MOBM320_IMAGE}" || rm -f $i
	done
}
