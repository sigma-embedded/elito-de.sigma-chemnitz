FILESEXTRAPATHS_prepend := "${THISDIR}/files:"
PRINC := "${@int('${PRINC}') + 1}"

SRC_URI += "file://longlong-compat.m4"

do_configure_prepend() {
	install -p -m 0644 ${WORKDIR}/longlong-compat.m4 m4/
}
