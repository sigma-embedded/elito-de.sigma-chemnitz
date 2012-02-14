ARNOLDBOOT	 = bootgen
ARNOLDBOOT_FLAGS =

ARNOLDBOOT_GOALS = arnoldboot-sdram arnoldboot-gdb arnoldboot-flash arnoldboot
LOCALGOALS	+= ${ARNOLDBOOT_GOALS}

arnoldboot-sdram:	ARNOLDBOOT_FLAGS=-d0
arnoldboot-gdb:		ARNOLDBOOT_FLAGS=-d2
arnoldboot-flash:	ARNOLDBOOT_FLAGS=-d3

arnoldboot-sdram arnoldboot-gdb arnoldboot-flash:	arnoldboot

arnoldboot:
	+$(_build_cmd) ${_k_all_target} zImage
	$(ARNOLDBOOT) ${ARNOLDBOOT_FLAGS} ${KERNEL_START_ADDRESS} ${KERNEL_START_ADDRESS} @arch/arm/boot/zImage > ${TFTP_IMAGE}

tftp:			arnoldboot
