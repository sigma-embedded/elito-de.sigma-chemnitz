srcdir ?= .
TARBALL = base-files.tar.bz2

dist:	$(TARBALL)

$(TARBALL):	$(srcdir)/rootfs
	tar cjf $(TARBALL) -C $^ . --owner root --group root
