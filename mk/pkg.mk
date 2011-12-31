OPKG_MAKE_INDEX =	opkg-make-index
FAKEROOT =		fakeroot
OPKG =			opkg-cl
OPKG_ENV =		env D='$(DESTDIR)'
OPKG_OPTS =		--force_postinstall

_pkgs =			$(foreach a,${PACKAGE_ARCHS},${DEPLOY_DIR_IPK}/$a/Packages)

_fakeroot =		$(_start) $(FAKEROOT)
_opkg_make_index =	$(_start) $(OPKG_MAKE_INDEX)
_opkg_conf =		${DEPLOY_DIR_IPK}/opkg.conf

LOCALGOALS =		pkg-regen pkg-update pkg-upgrade \
			pkg-install pkg-remove pkg-reinstall

pkg-regen:	$(wildcard ${_pkgs})
	@:

.PHONY:		pkg-update
pkg-update:	${_opkg_conf} pkg-regen
	$(_fakeroot) $(OPKG_ENV) $(OPKG) -o $(DESTDIR) -f '$<' $(OPKG_OPTS) update || :

.PHONY:		pkg-upgrade
pkg-upgrade:	${_opkg_conf} pkg-regen
	$(_fakeroot) $(OPKG_ENV) $(OPKG) -o $(DESTDIR) -f '$<' $(OPKG_OPTS) update || :
	$(_fakeroot) $(OPKG_ENV) $(OPKG) -o $(DESTDIR) -f '$<' $(OPKG_OPTS) upgrade

.PHONY:		pkg-install
pkg-install:	${_opkg_conf} pkg-update
	$(_fakeroot) $(OPKG_ENV) $(OPKG) -o $(DESTDIR) -f '$<' $(OPKG_OPTS) install ${P}

.PHONY:		pkg-reinstall
pkg-reinstall:	${_opkg_conf} pkg-update
	$(_fakeroot) $(OPKG_ENV) $(OPKG) -o $(DESTDIR) -f '$<' $(OPKG_OPTS) --force-reinstall install ${P}

.PHONY:		pkg-remove
pkg-remove:	${_opkg_conf}
	$(_fakeroot) $(OPKG_ENV) $(OPKG) -o $(DESTDIR) -f '$<' $(OPKG_OPTS) remove ${P}

${_pkgs}:${DEPLOY_DIR_IPK}/%/Packages:	${DEPLOY_DIR_IPK}/%
	$(_opkg_make_index) -r $@ -p $@ -l $@.filelist -m $<
	test -e $@	   # ensure that target really exists after this rule
	touch $@	   # make sure that timestamps are as expected
