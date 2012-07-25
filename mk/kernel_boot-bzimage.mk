UBOOT_GOALS	 = tftp-bzimage
LOCALGOALS	+= ${UBOOT_GOALS}

tftp-bzimage:	arch/$(ARCH)/boot/bzImage
	cat $< >$(TFTP_IMAGE)

tftp:	tftp-bzimage
