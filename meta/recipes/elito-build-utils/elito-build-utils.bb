DESCRIPTION = "Build utilities"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"

PV = "1.1"

SRC_URI = "\
  file://argv0.c \
  file://passwd-hash.c \
"

do_compile() {
    oe_runmake -e VPATH=${WORKDIR} \
      argv0 passwd-hash LDLIBS='-Wl,-as-needed -lcrypt'
}

do_install() {
    install -D -p -m 0755 argv0 ${D}${bindir}/elito-argv0
    install -D -p -m 0755 passwd-hash ${D}${bindir}/elito-passwd-hash
}

BBCLASSEXTEND = "native"
