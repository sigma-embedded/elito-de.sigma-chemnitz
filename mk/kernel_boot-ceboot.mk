CEBOOT_GOALS	=  ce-boot
LOCALGOALS	+= $(CEBOOT_GOALS)

ce-boot:
	+$(_build_cmd) ${_k_all_target} ceImage LOADADDR=$(KERNEL_START_ADDRESS) STARTADDR=$(KERNEL_START_ADDRESS) CE_LOADADDR=$(KERNEL_START_ADDRESS)
	-cat arch/$(ARCH)/boot/ceImage > $(TFTP_IMAGE)

tftp:	ce-boot
