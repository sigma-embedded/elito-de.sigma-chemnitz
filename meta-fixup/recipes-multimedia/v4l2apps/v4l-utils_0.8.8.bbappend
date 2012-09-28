PRINC := "${@int('${PRINC}') + 1}"

PATH =. "${S}/_bin:"
export PATH

do_configure_prepend() {
	mkdir -p _bin
	ln -sf /bin/false _bin/qmake-qt4
}
