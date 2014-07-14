FSLIOMUX = fsliomux-conv

VARIANTS = $(MACHINE)
UART =		uart2
DCD_DEVICES =	mmdc

pkginclude_DATA = $(foreach v,$(VARIANTS), $v-dcd.h $v-main.h $v-dts-pins.h)

all:	$(pkginclude_DATA)

%-dcd.h:	%-iomux.xml
	@rm -f $@ $@.tmp
	${FSLIOMUX} -F c -m fn -O '${DCD_DEVICES} ${UART}' -o '$@.tmp' -- $(filter %.xml,$^)
	@mv $@.tmp $@

%-main.h:	%-iomux.xml
	@rm -f $@ $@.tmp
	${FSLIOMUX} -F c -m fn -D '${DCD_DEVICES} ${UART}' -o '$@.tmp' -- $(filter %.xml,$^)
	@mv $@.tmp $@

.SECONDEXPANSION:

%-dts-pins.h:	%-iomux.xml %-postproc.sh $$(SOC_DTREE_PINS_%)
	@rm -f $@ $@.tmp
	${FSLIOMUX} -F dtree -m fn -D '${DCD_DEVICES}' -o '$@.tmp' -M '$(filter %.h,$^)' -P '$(abspath $(filter %-postproc.sh,$^))' -- '$(filter %.xml,$^)'
	@mv $@.tmp $@

%-postproc.sh:
	test -s '$@' || { rm -f '$@' && echo '#! /bin/sh' > $@; chmod a+x '$@'; }

install:	$(pkginclude_DATA)
	install -d -m 0755 ${DESTDIR}${pkgincludedir}
	install -p -m 0644 $(filter %.h,$^) ${DESTDIR}${pkgincludedir}/
