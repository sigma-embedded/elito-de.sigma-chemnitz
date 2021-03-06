SUMMARY = "Keith & Koep Trizeps6 boot helper"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM   = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"
COMPATIBLE_MACHINE = "kk-trizeps6"

_pv = "0.1.13"

SRCREV    = "f3158003702ce77a618237f7b4be0b84c2a03c35"
PV        = "${_pv}+gitr${SRCPV}"
PKGV      = "${_pv}+gitr${GITPKGV}"
PROVIDES += "virtual/ce-preloader"

SRC_URI   = "${ELITO_PUBLIC_GIT_REPO}/trizeps6-boot.git;protocol=git"
S         = "${WORKDIR}/git"

ALLOW_EMPTY_${PN}-dev = ""

TRIZEPS6_RESCUE_DEVICE ?= "${TRIZEPS6_BOOTDEVICE}"

inherit gitpkgv deploy

TRIZEPS6_PARTITION  ??= "sdhci-pxav2.1!2"
TRIZEPS6_BOOTDEVICE ??= "mmc"
KERNEL_LOGLEVEL   ??= "loglevel=5 quiet"

do_configure() {
    sed -i \
        -e 's:@TRIZEPS6_PARTITION@:${TRIZEPS6_PARTITION}:' \
        -e 's:@TRIZEPS6_BOOTDEVICE@:${TRIZEPS6_BOOTDEVICE}:' \
        -e 's:@TRIZEPS6_RESCUE_DEVICE@:${TRIZEPS6_RESCUE_DEVICE}:' \
        -e 's:@KERNEL_LOGLEVEL@:${KERNEL_LOGLEVEL}:' \
        -e 's:@KERNEL_CONSOLE@:${KERNEL_CONSOLE}:' \
        autoboot.bat
}

do_compile() {
    oe_runmake -e
}

do_install() {
    :
}

do_deploy() {
    install -D -p -m 0644 out/boot1.bin ${DEPLOYDIR}/boot.bin
    install -D -p -m 0644 autoboot.bat  ${DEPLOYDIR}/autoboot.bat
}
addtask deploy before do_build after do_compile
