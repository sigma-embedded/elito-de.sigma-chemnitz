LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"

OVERRIDES .= "${@base_contains('MACHINE_FEATURES','mobm320',':fs-mobm320','',d)}"
OVERRIDES .= "${@base_contains('MACHINE_FEATURES','u-boot',':fs-u-boot','',d)}"
OVERRIDES .= "${@base_contains('MACHINE_FEATURES','barebox',':fs-barebox','',d)}"
OVERRIDES .= "${@base_contains('MACHINE_FEATURES','ubifs',':fs-ubifs','',d)}"
OVERRIDES .= "${@base_contains('MACHINE_FEATURES','jffs2',':fs-jffs2','',d)}"
OVERRIDES .= "${@base_contains('MACHINE_FEATURES','arnoldboot',':fs-arnoldboot','',d)}"

IMAGE_FSTYPES_append_fs-mobm320	   = " bootenv"

EXTRA_IMAGEDEPENDS_append_fs-u-boot     = " u-boot"
EXTRA_IMAGEDEPENDS_append_fs-barebox    = " barebox"
EXTRA_IMAGEDEPENDS_append_fs-arnoldboot = " arnoldboot-native"
EXTRA_IMAGEDEPENDS_append_fs-mobm320    = " mobm320 mobm320-native"

inherit core-image
