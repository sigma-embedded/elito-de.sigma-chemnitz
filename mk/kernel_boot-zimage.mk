UBOOT_GOALS	 = tftp-zimage
LOCALGOALS	+= ${UBOOT_GOALS}

tftp-zimage:	arch/$(ARCH)/boot/zImage
	cat $< >$(TFTP_IMAGE)

tftp:	tftp-zimage
