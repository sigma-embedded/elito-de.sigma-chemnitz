DESCRIPTION = "Pseudo receipt to build + rm_work the 'pseudo' dependencies"
PACKAGES = ""
PACKAGE_ARCH = "all"
INHIBIT_DEFAULT_DEPS = "1"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

do_compile() {
    :
}

do_configure() {
    :
}

do_install() {
    :
}

do_package() {
    :
}

do_build[depends] += "pseudo-native:do_populate_sysroot"
do_build[depends] += "tar-replacement-native:do_populate_sysroot"
