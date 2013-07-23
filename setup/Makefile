GZIP =		gzip
TAR =		tar
WGET =		wget
GIT =		git

STATIC_CONTENT = \
	Makefile		\
	functions		\
	setup			\
	elito-proxy.tmpl

DOWNLOAD = \
	http://www.sigma-chemnitz.de/dl/elito/sources/sigma-ssl-0.6.10.tar.bz2

GITREV = $(shell set -x; ${GIT} describe --long --abbrev=40 HEAD 2>/dev/null | tr / _)
SED_CMD = \
	-e 's!@'GITREV'@!$(GITREV)!g'

_downloads =	$(addprefix packages/,$(notdir $(DOWNLOAD)))

elito-setup:	elito-setup.skel elito-setup.tar.gz
	cat $(filter %.skel,$^) | sed ${SED_CMD} > $@
	cat $(filter-out %.skel,$^) >> $@
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
