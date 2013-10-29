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

# avoid conflicts in '-c populate_sdk':
#  * check_conflicts_for: The following packages conflict with openssh:
#  * check_conflicts_for:       dropbear *
#  * opkg_install_cmd: Cannot install package openssh-dev.
ALLOW_EMPTY_${PN}-dev = ""
