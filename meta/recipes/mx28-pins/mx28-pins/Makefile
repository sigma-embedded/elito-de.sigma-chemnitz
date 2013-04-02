INSTALL = install
INSTALL_DATA = $(INSTALL) -p -m 0644

prefix = /usr/local
includedir = $(prefix)/include
datadir = $(prefix)/share

bootletsdir = $(includedir)/mach-$(MACHINE)
dtreedir = $(datadir)/mach-$(MACHINE)

all:	pins.h pins.dtsi

pins.%:	pinctrl mx28.regs $(MACHINE)-pin.setup
	@rm -f $@ $@.tmp
	bash $^ $(MODE) > $@.tmp
	@mv $@.tmp $@

%.h:	MODE=bootlets
%.dtsi:	MODE=dtree

install:	install-bootlets install-dtree


$(DESTDIR)$(bootletsdir) $(DESTDIR)$(dtreedir):
	mkdir -p $@

install-bootlets:	pins.h | $(DESTDIR)$(bootletsdir)
	$(INSTALL_DATA) $^ $(DESTDIR)$(bootletsdir)/

install-dtree:		pins.dtsi | $(DESTDIR)$(dtreedir)
	$(INSTALL_DATA) $^ $(DESTDIR)$(dtreedir)/

clean:
	rm -f pins.h pins.dtsi *.tmp
