PV = "0.1+gitr${SRCPV}"
SRC_URI = "git://git.kernel.org/pub/scm/linux/kernel/git/dwmw2/linux-firmware.git;protocol=git"
PACKAGE_ARCH = "all"
LICENSE = "unknown"

INHIBIT_DEFAULT_DEPS = "1"

do_install() {
    cd ../git

    install -d ${D}/lib/firmware
    install -p -m 0644 libertas/*.bin ${D}/lib/firmware/
}

PACKAGES = " \
	libertas-sd8686-v9	\
	libertas-sd8686-v8	\
"

FILES_libertas-sd8686-v9 = "/lib/firmware/sd8686_v9*"
FILES_libertas-sd8686-v8 = "/lib/firmware/sd8686_v8*"

RRECOMMENDS_libertas-sd8686-v9 = "udev-firmware"
RRECOMMENDS_libertas-sd8686-v8 = "udev-firmware"
