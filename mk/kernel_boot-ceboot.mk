CEBOOT_GOALS	=  ce-boot
LOCALGOALS	+= $(CEBOOT_GOALS)

ce-boot:
	+$(_build_cmd) _all ceImage LOADADDR=$(KERNEL_START_ADDRESS) STARTADDR=$(KERNEL_START_ADDRESS)
	-cat arch/$(ARCH)/boot/ceImage > $(TFTP_IMAGE)

tftp:	ce-boot
