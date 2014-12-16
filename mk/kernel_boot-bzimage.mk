UBOOT_GOALS	 = tftp-bzimage
LOCALGOALS	+= ${UBOOT_GOALS}

tftp-bzimage:	${KBUILD_BOOT_DIR}/bzImage
	cat $< >$(TFTP_IMAGE)

tftp:	tftp-bzimage
