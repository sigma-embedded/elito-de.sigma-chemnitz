OVERRIDES .= ${@base_contains('MACHINE_FEATURES','mobm320',':fs-mobm320','',d)}
OVERRIDES .= ${@base_contains('MACHINE_FEATURES','u-boot',':fs-u-boot','',d)}
OVERRIDES .= ${@base_contains('MACHINE_FEATURES','barebox',':fs-barebox','',d)}
OVERRIDES .= ${@base_contains('MACHINE_FEATURES','ubifs',':fs-ubifs','',d)}
OVERRIDES .= ${@base_contains('MACHINE_FEATURES','jffs2',':fs-jffs2','',d)}
OVERRIDES .= ${@base_contains('MACHINE_FEATURES','arnoldboot',':fs-arnoldboot','',d)}

PROVIDES = "virtual/elito-image"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"

DEPENDS = "	\
	${MACHINE_TASK_PROVIDER}	\
	${IMAGE_DEPENDS}		\
"

IMAGE_LINGUAS = ""
IMAGE_INSTALL = "${MACHINE_TASK_PROVIDER}"
IMAGE_BOOT    = ""

IMAGE_FSTYPES_append_fs-mobm320 = " bootenv"

IMAGE_DEPENDS_append_fs-u-boot     = " u-boot"
IMAGE_DEPENDS_append_fs-barebox    = " barebox"
IMAGE_DEPENDS_append_fs-arnoldboot = " arnoldboot-native"
IMAGE_DEPENDS_append_fs-mobm320    = " mobm320 mobm320-native"

export OVERRIDES
export DEPENDS
export IMAGE_DEPENDS

do_rootfs_prepend () {
	PATH=$PATH:/usr/local/sbin:/usr/sbin:/sbin
}

inherit image ubi arnoldboot elito-final
