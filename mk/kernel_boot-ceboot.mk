CEBOOT_GOALS	=  ce-boot ce-boot-pre.bin ceImage tftp-ce-boot
CEBOOT_GOALS	+= arch/$(ARCH)/boot/ceImage
LOCALGOALS	+= $(CEBOOT_GOALS)

CE_DATA2BIN	=  ce-data2bin
CE_PRELOADER	=  $(DEPLOY_DIR_IMAGE)/boot.bin
CE_ADDRESS	=  $$(( $(KERNEL_START_ADDRESS) - $(if $(CE_PRELOADER),4096,0) ))

_ce_input	=  $(if $(CE_PRELOADER),ce-boot-pre.bin,arch/$(ARCH)/boot/zImage)

ce-boot-pre.bin:	$(CE_PRELOADER) arch/$(ARCH)/boot/zImage
	rm -f $@
	dd if=$(filter %$(CE_PRELOADER),$^) bs=4096 of=$@ conv=sync
	cat $(filter %/zImage,$^) >> $@

arch/$(ARCH)/boot/ceImage:	$(_ce_input)
	rm -f $@ $@.tmp
	$(CE_DATA2BIN) $< $(CE_ADDRESS) $(CE_ADDRESS) >$@.tmp
	mv $@.tmp $@

ceImage ce-boot:	arch/$(ARCH)/boot/ceImage

tftp-ce-boot:	ceImage
	-cat arch/$(ARCH)/boot/ceImage > $(TFTP_IMAGE)

tftp:	tftp-ce-boot
