_elito_skip := "${@elito_skip(d, 'rescuekernel', None)}"

DESCRIPTION = "ELiTo rescue kernel"
SECTION = "kernel"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"

PR = "r0"

SUPPORTED_MACHINE_FEATURES = "ce-bootme"

IMAGE_INIT_MANAGER = "busybox"
DISTRO_FEATURES = "ipv4 mmc headless leds initrd nonfs"
MACHINE_FEATURES_BACKFILL = ""
MACHINE_FEATURES := "${@elito_intersect('MACHINE_FEATURES', 'SUPPORTED_MACHINE_FEATURES', d)}"
KERNEL_IMAGETYPE = "zImage"

RESCUE_KERNEL_TFTP_IMAGE ?= ""
RESCUE_KERNEL_DEFCONFIG ?= "${KERNEL_DEFCONFIG}"
IMAGE_RESCUE_ROOTFS ?= "${IMAGE_ROOTFS}-rescue"

_tftp_image = "${RESCUE_KERNEL_TFTP_IMAGE}"
_defconfig = "${RESCUE_KERNEL_DEFCONFIG}"

OVERRIDE_KERNEL_CMDLINE	= "1"

MACHINE_HOSTNAME_append = "-rescue"

KERNEL_CMDLINE_SOURCE = "force"
KERNEL_CMDLINE = "INVALID"
KERNEL_IMAGE_MAXSIZE = "INVALID"
KERNEL_IMAGE_BASE_NAME = "rescue-${KERNEL_IMAGETYPE}-${PV}-${PR}-${MACHINE}-${DATETIME}"
KERNEL_IMAGE_SYMLINK_NAME = "rescue-${KERNEL_IMAGETYPE}-${MACHINE}"

KERNEL_CONFIG_DISABLE_OPTS ="\
  MODULES NETWORK_FILESYSTEMS SUNRPC MEDIA_SUPPORT CAN FTRACE DYNAMIC_DEBUG SOUND \
  AUTOFS4_FS FUSE_FS CUSE INPUT_TOUCHSCREEN WIRELESS WLAN PPP BT \
  USB_GADGET USB_ACM USB_PRINTER USB_SDM USB_TMC USB_ETH USB_MON \
  CMDLINE_FROM_BOOTLOADER"

KERNEL_EARLY_OPTIONS_FUNCS += "elito_kernel_rescue_early_options"

RESCUE_IMAGE_NAME ?= "${DEPLOY_DIR_IMAGE}/elito-rescue-image-${MACHINE}.cpio"

def elito_kernel_rescue_early_options(d, o):
    import elito

    YES = elito.kernel_feature.YES

    o.extend(map(lambda x: [True, x, False],
                 d.getVar('KERNEL_CONFIG_DISABLE_OPTS').split()))

    return o

inherit kernel
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

do_configure[depends]    += "virtual/rescue-image:do_rootfs"

do_builddeploy[nostamp]   = "1"
do_deploy_setscene[nostamp] = "1"

do_install[noexec]	    = "1"
do_populate_sysroot[noexec]  = "1"
do_package[noexec]	     = "1"
do_package_write_ipk[noexec] = "1"
do_package_write_deb[noexec] = "1"
do_package_write_rpm[noexec] = "1"

PACKAGES = ""

do_generate_initramfs_source() {
    :
}
