CEBOOT_GOALS	=  ce-boot ce-boot-pre.bin ceImage tftp-ce-boot
CEBOOT_GOALS	+= ${KBUILD_BOOT_DIR}/ceImage
LOCALGOALS	+= $(CEBOOT_GOALS)

CE_DATA2BIN	=  ce-data2bin
CE_PRELOADER	=  $(DEPLOY_DIR_IMAGE)/boot.bin
CE_ADDRESS	=  $$(( $(KERNEL_START_ADDRESS) - $(if $(CE_PRELOADER),4096,0) ))

_ce_input	=  $(if $(CE_PRELOADER),ce-boot-pre.bin,${KBUILD_BOOT_DIR}/zImage)

ce-boot-pre.bin:	$(CE_PRELOADER) ${KBUILD_BOOT_DIR}/zImage
	rm -f $@
	dd if=$(filter %$(CE_PRELOADER),$^) bs=4096 of=$@ conv=sync
	cat $(filter %/zImage,$^) >> $@

${KBUILD_BOOT_DIR}/ceImage:	$(_ce_input)
	rm -f $@ $@.tmp
	$(CE_DATA2BIN) $< $(CE_ADDRESS) $(CE_ADDRESS) >$@.tmp
	mv $@.tmp $@

ceImage ce-boot:	${KBUILD_BOOT_DIR}/ceImage

tftp-ce-boot:	${KBUILD_BOOT_DIR}/ceImage
	-cat $< > $(TFTP_IMAGE)

tftp:	tftp-ce-boot
