DESCRIPTION = "Build utilities"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"

SRC_URI = "file://argv0.c"

do_compile() {
    oe_runmake VPATH=${WORKDIR} \
      argv0
}

do_install() {
    install -D -p -m 0755 argv0 ${D}${bindir}/elito-argv0
}

BBCLASSEXTEND = "native"
