LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"

PV = "0.1.1"

OVERRIDES .= "${@bb.utils.contains('MACHINE_FEATURES', 'imx-bootlets', \
                                   ':img-imx-bootlets','',d)}"

ORIG_IMAGE_FSTYPES := "${IMAGE_FSTYPES}"

_default_image_rescue_rootfs := "${IMAGE_ROOTFS}-rescue"

SUPPORTED_IMAGE_FEATURES = "debug-tweaks"

IMAGE_RESCUE_ROOTFS ?= "${_default_image_rescue_rootfs}"
IMAGE_LINGUAS := ""
IMAGE_FEATURES := "${@elito_intersect('IMAGE_FEATURES', 'SUPPORTED_IMAGE_FEATURES', d)}"
IMAGE_ROOTFS := "${IMAGE_RESCUE_ROOTFS}"
IMAGE_FSTYPES = "cpio.xz"

imagedeps = ""
imagedeps_img-imx-bootlets = "imx-bootlets elftosb-native"

DEPENDS += "${imagedeps}"
IMAGE_INSTALL = "\
  virtual/rescue-files \
  busybox \
  base-passwd \
  sysvinit \
  ${@bb.utils.contains('ORIG_IMAGE_FSTYPES', 'jffs2', 'mtd-utils-jffs2', '', d)} \
  ${@bb.utils.contains('ORIG_IMAGE_FSTYPES', 'ubifs', \
                       'mtd-utils-ubifs elito-rescue-base-sysv-scan-ubi', '', d)} \
  ${@bb.utils.contains('MACHINE_FEATURES', 'mtd', \
                       'mtd-utils elito-rescue-base-sysv-scan-mtd', '', d)} \
"

ROOTFS_POSTINSTALL_COMMAND += "rescue_fixup_rootfs"
IMAGE_PREPROCESS_COMMAND += "rescue_cleanup_rootfs"
BAD_RECOMMENDATIONS += "busybox-syslog-systemd systemd"

inherit image elito-image

PROVIDES += "virtual/rescue-image"
DEPENDS += "sysvinit elito-base-files elito-image-stream-native"

rootfs_install_all_locales() {
    :
}

rescue_fixup_rootfs() {
    cd ${IMAGE_ROOTFS}

    rm -rf dev

    for i in run-postinsts stop-bootlogd syslog busybox-udhcpc busybox-cron syslog.busybox; do
	rm -f etc/init.d/$i etc/rc*.d/[SK][0-9][0-9]$i
    done

    rm -f etc/inittab

    (
        set -- ${SERIAL_CONSOLE}
        test -z "$2" || \
            grep -q "^$2\$" etc/securetty 2>/dev/null || \
            echo "$2" >> etc/securetty
    )

    cat <<EOF >etc/inittab
id:5:initdefault:
si::sysinit:/etc/init.d/rcS
l3:3:wait:/etc/init.d/rc 3
S:2345:respawn:/sbin/getty ${SERIAL_CONSOLE}
EOF

    rm -f etc/mtab
    ln -s ../proc/mounts etc/mtab
    rm -f var/lock

    install -d -m 0755 run run/lock
    install -d -m 0755 dev proc sys mnt media run tmp var/log root

    ln -s ../run/lock var/lock

    mknod -m 0600 dev/console c 5 1

    cd -
}

rescue_cleanup_rootfs() {
    cd ${IMAGE_ROOTFS}

    rm -rf lib/modules
    rm -rf var/lib/opkg
    rm -rf usr/lib/opkg
    rm -f sbin/ldconfig

    rm -vf lib/systemd/system/*.service
    rmdir lib/systemd/system || :
    rmdir lib/systemd || :

    ! test -e lib/systemd

    rm -rf etc/systemd

    cd -
}

#### copied from core-image.bbclass ####

# Create /etc/timestamp during image construction to give a reasonably
# sane default time setting
ROOTFS_POSTPROCESS_COMMAND += "rootfs_update_timestamp ; "

# Zap the root password if debug-tweaks feature is not enabled
ROOTFS_POSTPROCESS_COMMAND += "${@\
  bb.utils.contains('IMAGE_FEATURES', 'debug-tweaks', \
                    '', 'zap_empty_root_password ; ',d)}"

# Allow openssh accept empty password login if both debug-tweaks and
# ssh-server-openssh are enabled
ROOTFS_POSTPROCESS_COMMAND += "${@\
  bb.utils.contains('IMAGE_FEATURES', 'debug-tweaks ssh-server-openssh', \
                    'openssh_allow_empty_password; ', '',d)}"
