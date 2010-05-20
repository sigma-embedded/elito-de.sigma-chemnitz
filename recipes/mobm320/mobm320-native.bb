DESCRIPTION      = "Initial bootloader for PXA320"
SECTION          = "bootloaders"
PRIORITY         = "optional"
LICENSE          = "GPLv3"
PACKAGE_ARCH     = "${MACHINE_ARCH}"
PV               = "0.3.6"
PR               = "r1"
SRC_URI          = "${ELITO_MIRROR}/elito-mobm320-${PV}.tar.bz2"
S                = ${WORKDIR}/elito-mobm320-${PV}

SRC_URI[md5sum] = "646968ca76caed3ed16bdbf2f3a9728b"
SRC_URI[sha256sum] = "ab042cad840b5ff0a05464cfdb3bea1952105416321544bd8ff818555d749a52"

PACKAGES         = "${PN}-dbg ${PN}"
FILES_${PN}      = ${bindir}/mobm320-create-env

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
