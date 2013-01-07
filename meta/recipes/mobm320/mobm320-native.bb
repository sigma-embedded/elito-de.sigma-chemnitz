_elito_skip := "${@elito_skip(d, 'mobm320')}"

require mobm320-common.inc
inherit native

FILES_${PN} = "${bindir}/mobm320-create-env"

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
