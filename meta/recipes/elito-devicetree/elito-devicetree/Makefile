DTC ?= dtc
INSTALL ?= install
INSTALL_DATA = $(INSTALL) -p -m 0644

abs_top_srcdir = $(dir $(abspath $(firstword $(MAKEFILE_LIST))))

datadir = $(prefix)/share
dtreedir = $(datadir)/mach-$(MACHINE)

MACH_DTSI_mx28 = imx28.dtsi pins.dtsi skeleton.dtsi

VPATH = ${abs_top_srcdir} $(MACHINE_INCDIR)

AM_DTC_CPPFLAGS = \
  -nostdinc -x assembler-with-cpp \
  -I '$(MACHINE_INCDIR)' -I .

o = out/

define _linkfile
ln -s '$1' '$2/'

endef

all:		$(MACHINE).dtb

$o$(MACHINE)-preproc.dtb:	$(MACHINE).dts $(MACH_DTSI_$(SOC_FAMILY)) | $o
	-rm -f $(addprefix $o/,$(notdir $(wildcard $^)))
	$(CPP) $(AM_DTC_CPPFLAGS) $(DTC_CPPFLAGS) -o $@ $<
	$(foreach f,$(abspath $(wildcard $^)),$(call _linkfile,$f,$o))

$(MACHINE).dtb:	$o$(MACHINE)-preproc.dtb
	cd $o && $(DTC) -I dts $(abspath $<) -o $(abspath $@) -O dtb

$(DESTDIR)$(dtreedir):
	mkdir -p $@

install:	$(MACHINE).dtb | $(DESTDIR)$(dtreedir)
	$(INSTALL_DATA) $< $(DESTDIR)$(dtreedir)/

clean:
	rm -f $(MACHINE).dtb

$o:
	mkdir -p $@
