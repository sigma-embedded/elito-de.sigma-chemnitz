srcdir ?= .
TARBALL = base-files.tar.bz2

dist:		BASEDIR=$(srcdir)/rootfs
dist-rescue:	BASEDIR=$(srcdir)/rootfs-rescue

dist dist-rescue:	$(TARBALL)

$(TARBALL):
	tar cjf $(TARBALL) -C $(BASEDIR) . --owner root --group root
