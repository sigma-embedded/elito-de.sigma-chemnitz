UBOOT_GOALS	 = tftp-uboot
LOCALGOALS	+= ${UBOOT_GOALS}

tftp-uboot:
	+$(_build_cmd) _all uImage
	cat arch/arm/boot/uImage >$(TFTP_IMAGE)

tftp:	tftp-uboot
