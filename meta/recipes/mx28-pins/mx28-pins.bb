# --*- python -*--
DESCRIPTION  = "${MACHINE} pin setup"
LICENSE      = "GPLv3"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"

SRC_URI = "\
  file://Makefile \
  file://mx28.regs \
  file://pinctrl \
  file://${MACHINE}-pin.setup \
"

INHIBIT_DEFAULT_DEPS = "1"
EXTRA_OEMAKE = "\
  MACHINE=${MACHINE} \
  VPATH=${WORKDIR} prefix=${prefix} datadir=${datadir} \
"

PACKAGE_ARCH = "${MACHINE_ARCH}"

do_configure() {
    ln -sf ../Makefile .
}

do_install() {
    oe_runmake install DESTDIR=${D} 
}

FILES_${PN}-dev += "${datadir}/mach-${MACHINE}/*"
