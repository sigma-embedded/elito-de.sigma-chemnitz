ARNOLDBOOT	 = bootgen
ARNOLDBOOT_FLAGS =

ARNOLDBOOT_GOALS = arnoldboot-sdram arnoldboot-gdb arnoldboot-flash arnoldboot
LOCALGOALS	+= ${ARNOLDBOOT_GOALS}

arnoldboot-sdram:	ARNOLDBOOT_FLAGS=-d0
arnoldboot-gdb:		ARNOLDBOOT_FLAGS=-d2
arnoldboot-flash:	ARNOLDBOOT_FLAGS=-d3

arnoldboot-sdram arnoldboot-gdb arnoldboot-flash:	arnoldboot

arnoldboot:	arch/arm/boot/zImage
	$(ARNOLDBOOT) ${ARNOLDBOOT_FLAGS} ${KERNEL_START_ADDRESS} ${KERNEL_START_ADDRESS} @$< > ${TFTP_IMAGE}

tftp:			arnoldboot
