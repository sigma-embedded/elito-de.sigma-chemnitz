DESCRIPTION = "Rescue target"
LICENSE     = "GPLv3"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"

PACKAGE_ARCH = "${MACHINE_ARCH}"

_MACH_DEPENDS = "elito-rescue-kernel"
_MACH_DEPENDS_mx6 = "elito-mx6-load"

DEPENDS = "elito-image-stream-native ${_MACH_DEPENDS}"

do_distribute_sources() {
}

# We only need the packaging tasks - disable the rest
do_fetch[noexec] = "1"
do_unpack[noexec] = "1"
do_patch[noexec] = "1"
do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_install[noexec] = "1"
do_populate_sysroot[noexec] = "1"
