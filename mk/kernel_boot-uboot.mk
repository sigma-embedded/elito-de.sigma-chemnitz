UBOOT_GOALS	 = tftp-uboot
LOCALGOALS	+= ${UBOOT_GOALS}

tftp-uboot:
	+$(_build_cmd) ${_k_all_target} uImage
	cat arch/arm/boot/uImage >$(TFTP_IMAGE)

tftp:	tftp-uboot
