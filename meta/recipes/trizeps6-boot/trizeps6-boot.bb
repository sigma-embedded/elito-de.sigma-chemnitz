SUMMARY = "Keith & Koep Trizeps6 boot helper"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"

COMPATIBLE_MACHINE = "kk-trizeps6"

SRCREV = "e34bd5f82e611117ae3fcc8a5f55fc9926db542f"
_pv = "0.1.2"
PV = "${_pv}+gitr${SRCPV}"
PKGV = "${_pv}+gitr${GITPKGV}"

SRC_URI = "${ELITO_GIT_REPO}/pub/trizeps6-boot.git;protocol=git"

inherit gitpkgv deploy

S = "${WORKDIR}/git"

do_compile() {
    oe_runmake
}

do_install() {
    :
}

do_deploy() {
    install -D -p -m 0644 out/boot1.bin ${DEPLOYDIR}/boot.bin
    install -D -p -m 0644 autoboot.bat  ${DEPLOYDIR}/autoboot.bat
}
addtask deploy before do_build after do_compile
