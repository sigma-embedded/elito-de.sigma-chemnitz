_elito_skip := "${@elito_skip(d, 'rescuekernel', None, 'PROJECT_FEATURES')}"

DESCRIPTION = "ELiTo rescue kernel"
SECTION = "kernel"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"

SUPPORTED_MACHINE_FEATURES = "ce-bootme"

IMAGE_INIT_MANAGER = "busybox"
DISTRO_FEATURES = "ipv4 mmc headless leds initrd nonfs"
MACHINE_FEATURES_BACKFILL = ""
MACHINE_FEATURES := "${@elito_intersect('MACHINE_FEATURES', 'SUPPORTED_MACHINE_FEATURES', d)}"
KERNEL_IMAGETYPE = "zImage"
STAGING_KERNEL_BUILDDIR = "${TMPDIR}/work-shared/${MACHINE}/kernel-rescue-artifacts"

RESCUE_KERNEL_TFTP_IMAGE ?= ""
RESCUE_KERNEL_DEFCONFIG ?= "${KERNEL_DEFCONFIG}"
IMAGE_RESCUE_ROOTFS ?= "${IMAGE_ROOTFS}-rescue"
PROVIDES = "virtual/elito-rescue-kernel"

_tftp_image = "${RESCUE_KERNEL_TFTP_IMAGE}"
_defconfig = "${RESCUE_KERNEL_DEFCONFIG}"

OVERRIDE_KERNEL_CMDLINE	= "1"

MACHINE_HOSTNAME_append = "-rescue"

KERNEL_CMDLINE_SOURCE = "force"
KERNEL_CMDLINE = "INVALID"
KERNEL_IMAGE_MAXSIZE = "INVALID"
KERNEL_IMAGE_BASE_NAME = "rescue-${PV}-${PR}-${MACHINE}-${DATETIME}"
KERNEL_IMAGE_SYMLINK_NAME = "rescue-${MACHINE}"

KERNEL_CONFIG_DISABLE_OPTS ="\
  RD_GZIP RD_LZMA RD_LZO KGDB \
  NET_SCHED VLAN_8021Q NET_PKTGEN FIB_RULES BLK_DEV_NBD NET_TEAM SLIP FSCACHE \
  MODULES NETWORK_FILESYSTEMS SUNRPC MEDIA_SUPPORT CAN FTRACE DYNAMIC_DEBUG SOUND \
  AUTOFS4_FS FUSE_FS CUSE INPUT_TOUCHSCREEN WIRELESS WLAN PPP BT \
  USB_GADGET USB_ACM USB_PRINTER USB_SDM USB_TMC USB_ETH USB_MON \
  CMDLINE_FROM_BOOTLOADER IP_PNP N_GSM"

KERNEL_OPTIONS_FUNCS += "elito_kernel_rescue_options"

RESCUE_IMAGE_NAME ?= "${DEPLOY_DIR_IMAGE}/elito-rescue-image-${MACHINE}.cpio.xz"

def elito_kernel_rescue_options(d, o):
    import elito

    YES = elito.kernel_feature.YES

    o.append([True,
              [ 'UEVENT_HELPER_PATH', '/sbin/hotplug' ],
              [ 'CRYPTO', True ],
              [ 'CRYPTO_USER_API_HASH', True ],
              [ 'CRYPTO_SHA1', True ]])

    return o

inherit kernel
inherit kernelsrc
require elito-kernel.inc

# do not move to top
INITRAMFS_SOURCE = "${RESCUE_IMAGE_NAME}"

# remove 'virtual/kernel' from provides
python () {
    p = (d.getVar("PROVIDES", False) or '').split()
    p = filter(lambda x: x != 'virtual/kernel', p)
    d.setVar("PROVIDES", ' '.join(p))
}

python () {
    def check_defined(varname, d):
        if d.getVar(varname, False) == 'INVALID':
            raise Exception("variable '%s' not defined in .bbappend" % varname)

    check_defined('KERNEL_CMDLINE', d)
    check_defined('KERNEL_IMAGE_MAXSIZE', d)
}

do_configure[depends]    += "virtual/rescue-image:do_image_cpio"

do_unpack[cleandirs]	    = ""
do_unpack[noexec]	    = "1"
do_install[noexec]	    = "1"
do_populate_sysroot[noexec]  = "1"
do_package_write_ipk[noexec] = "1"
do_package_write_deb[noexec] = "1"
do_package_write_rpm[noexec] = "1"

PACKAGEFUNCS_remove = "emit_depmod_pkgdata"

python __anonymous () {
    d.setVar('PACKAGES', '')
}

do_generate_initramfs_source() {
    :
}
