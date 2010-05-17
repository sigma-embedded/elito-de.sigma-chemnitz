GZIP =		gzip
TAR =		tar
WGET =		wget

STATIC_CONTENT = \
	Makefile		\
	functions		\
	setup			\
	elito-proxy.tmpl

DOWNLOAD = \
	http://www.sigma-chemnitz.de/dl/elito/sources/sigma-ssl-0.5.tar.bz2

_downloads =	$(addprefix packages/,$(notdir $(DOWNLOAD)))

elito-setup:	elito-setup.skel elito-setup.tar.gz
	cat $^ > $@
	chmod 0755 $@

elito-setup.tar:	$(STATIC_CONTENT) $(_downloads)
	$(TAR) cf $@ $^

%.gz:		%
	rm -f $@ $@.tmp
	$(GZIP) -c -9 < $< > $@.tmp
	mv $@.tmp $@

clean:
	rm -f elito-setup *.tar *.tar.gz
	rm -rf packages/

define do-download
packages/$$(notdir $1):
	-mkdir -p $$(@D)
	rm -f $$@.tmp $$@
	$$(WGET) "$1" -O $$@.tmp
	mv $$@.tmp $$@
endef

$(foreach u,$(DOWNLOAD),$(eval $(call do-download,$u)))
