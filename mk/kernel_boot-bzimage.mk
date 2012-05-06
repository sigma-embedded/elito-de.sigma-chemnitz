UBOOT_GOALS	 = tftp-bzimage
LOCALGOALS	+= ${UBOOT_GOALS}

tftp-bzimage:
	+$(_build_cmd) ${_k_all_target} bzImage
	cat arch/$(ARCH)/boot/bzImage >$(TFTP_IMAGE)

tftp:	tftp-bzimage
