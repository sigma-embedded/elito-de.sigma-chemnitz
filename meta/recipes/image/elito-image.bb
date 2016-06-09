PROVIDES = "virtual/elito-image"

EXTRA_IMAGEDEPENDS = "	\
	${MACHINE_TASK_PROVIDER}	\
"

IMAGE_INSTALL = "${MACHINE_TASK_PROVIDER}"
IMAGE_BOOT    = ""

IMAGE_PREPROCESS_COMMAND += "elito_cleanup_boot"

PATH_append = ":/usr/local/sbin:/usr/sbin:/sbin"

elito_cleanup_boot() {
	rm -f ${IMAGE_ROOTFS}/boot/*
}

systemd_enable_sysrq_b() {
	if grep -q "^kernel\.sysrq.*=.*16" "${IMAGE_ROOTFS}${libdir}/sysctl.d/50-default.conf" 2>/dev/null; then
		echo 'kernel.sysrq = 1' > "${IMAGE_ROOTFS}${libdir}/sysctl.d/80-sysrq.conf"
        fi
}

require elito-image.inc
inherit ubi arnoldboot elito-final
inherit elito-image

# enable sysrq-b with systemd
ROOTFS_POSTPROCESS_COMMAND += "${@\
  bb.utils.contains('IMAGE_FEATURES', 'debug-tweaks', \
                    'systemd_enable_sysrq_b; ', '', d)}"
