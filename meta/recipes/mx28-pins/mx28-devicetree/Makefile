DTC ?= dtc
INSTALL ?= install
INSTALL_DATA = $(INSTALL) -p -m 0644

datadir = $(prefix)/share
dtreedir = $(datadir)/mach-$(MACHINE)


all:		$(MACHINE).dtb

$(MACHINE).dtb:	$(MACHINE).dts imx28.dtsi skeleton.dtsi pins.dtsi
	$(DTC) -I dts $< -o $@ -O dtb

$(DESTDIR)$(dtreedir):
	mkdir -p $@

install:	$(MACHINE).dtb | $(DESTDIR)$(dtreedir)
	$(INSTALL_DATA) $< $(DESTDIR)$(dtreedir)/

clean:
	rm -f $(MACHINE).dtb
