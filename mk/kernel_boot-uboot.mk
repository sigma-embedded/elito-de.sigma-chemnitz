UBOOT_GOALS	 = tftp-uboot
LOCALGOALS	+= ${UBOOT_GOALS}

tftp-uboot:	${KBUILD_BOOT_DIR}/uImage
	cat $< >$(TFTP_IMAGE)

tftp:	tftp-uboot
