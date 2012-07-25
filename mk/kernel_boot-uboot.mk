UBOOT_GOALS	 = tftp-uboot
LOCALGOALS	+= ${UBOOT_GOALS}

tftp-uboot:	arch/$(ARCH)/boot/uImage
	cat $< >$(TFTP_IMAGE)

tftp:	tftp-uboot
