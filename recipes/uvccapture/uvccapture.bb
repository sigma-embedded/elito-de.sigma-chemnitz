PV = "0.5"
PR = "r0"
LICENSE = "GPLv2+"
HOMEPAGE = "http://staticwave.ca/source/uvccapture/"
DESCRIPTION = " \
The purpose of this software is to capture an image from a USB webcam at a \
specified interval, and save it to a JPEG file, no other formats are supported. \
\
Right now this software is really a hack, since still image support is not yet \
available in the UVC driver. The program continually polls the UVC driver in \
MJPEG mode, and at a specified interval writes a JPEG header and a single frame \
to file, creating a JPEG image."

SRC_URI = "http://staticwave.ca/source/uvccapture/uvccapture-${PV}.tar.bz2"

DEPENDS = "jpeg"

do_compile() {
    make CC="$CC $CFLAGS $LDFLAGS"
}

do_install() {
    mkdir -p ${D}/usr/bin
    install -p -m 0755 uvccapture ${D}/usr/bin
}
