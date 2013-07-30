PRINC := "${@int('${PRINC}') + 1}"
FILESEXTRAPATHS_prepend := "${THISDIR}:"

SRC_URI += "\
  file://etc-dropbear.mount \
  file://run-dropbear.service \
"

do_install_append() {
    install -p -m 0644 \
        ${WORKDIR}/etc-dropbear.mount \
        ${WORKDIR}/run-dropbear.service \
	${D}${systemd_unitdir}/system/
}

FILES_${PN} += "\
  ${systemd_unitdir}/system/etc-dropbear.mount \
  ${systemd_unitdir}/system/run-dropbear.service"
