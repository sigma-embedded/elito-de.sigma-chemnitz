PV		= "0.1+gitr${SRCPV}"
PR		= "r2"
LICENSE		= "unknown"
PACKAGE_ARCH	= "all"

SRC_URI = "git://git.kernel.org/pub/scm/linux/kernel/git/dwmw2/linux-firmware.git;protocol=git"

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

RRECOMMENDS_libertas-sd8686-v9 = "${FIRMWARE_LOADER_PROVIDER}"
RRECOMMENDS_libertas-sd8686-v8 = "${FIRMWARE_LOADER_PROVIDER}"
