DTC ?= dtc
INSTALL ?= install
INSTALL_DATA = $(INSTALL) -p -m 0644

datadir = $(prefix)/share
dtreedir = $(datadir)/mach-$(MACHINE)

MACH_DTSI_mx28 = imx28.dtsi pins.dtsi

AM_DTC_CPPFLAGS = \
  -nostdinc -x assembler-with-cpp \
  -I '$(MACHINE_INCDIR)' -I .

all:		$(MACHINE).dtb

$(MACHINE)-preproc.dtb:	$(MACHINE).dts $(MACH_DTSI_$(SOC_FAMILY))
	$(CPP) $(AM_DTC_CPPFLAGS) $(DTC_CPPFLAGS) -o $@ $<

$(MACHINE).dtb:	$(MACHINE)-preproc.dtb
	$(DTC) -I dts $< -o $@ -O dtb

$(DESTDIR)$(dtreedir):
	mkdir -p $@

install:	$(MACHINE).dtb | $(DESTDIR)$(dtreedir)
	$(INSTALL_DATA) $< $(DESTDIR)$(dtreedir)/

clean:
	rm -f $(MACHINE).dtb
