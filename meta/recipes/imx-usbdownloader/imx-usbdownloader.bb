HOMEPAGE = "https://community.freescale.com/thread/249793"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://libusb_handler.c;md5=04eebdebb9cb9d32c0794e92b011e241"
SRC_URI = "\
  https://community.freescale.com/servlet/JiveServlet/download/256304-205358/693-linux_usbdownloader.tgz \
  file://claimusb.patch \
  file://udev-wait.patch \
"

SRC_URI[md5sum] = "74fd69ad77bfa07df2c94f3e7cd6da13"
SRC_URI[sha256sum] = "4a2c1995d5c7f4b783cc09bd27e9d0e4fc39940f70ce6bf2e07af13d34325eaf"

DEPENDS = "libusb libudev"

S = "${WORKDIR}/usbdownloader"
BBCLASSEXTEND = "native"

do_compile() {
    LIBUSB_FLAGS=`pkg-config --libs --cflags libusb libudev`

    ${CC} ${CFLAGS} ${LDFLAGS} main.c libusb_handler.c \
        ${LIBUSB_FLAGS} -o imx-usbdownloader
}

do_install() {
    install -D -p -m 0755 imx-usbdownloader ${D}${sbindir}/imx-usbdownloader
}
