IPKG_MAKE_INDEX =	ipkg-make-index
FAKEROOT =		fakeroot
IPKG =			opkg-cl

_pkgs =			$(foreach a,${PACKAGE_ARCHS},${DEPLOY_DIR_IPK}/$a/Packages)

_fakeroot =		$(_start) $(FAKEROOT)
_ipkg_make_index =	$(_start) $(IPKG_MAKE_INDEX)

pkg-regen:	$(wildcard ${_pkgs})
	@:

.PHONY:		pkg-update
pkg-update:	${STAGING_ETCDIR_NATIVE}/opkg.conf pkg-regen
	$(_fakeroot) $(IPKG) -o $(DESTDIR) -f '$<' update || :

.PHONY:		pkg-upgrade
pkg-upgrade:	${STAGING_ETCDIR_NATIVE}/opkg.conf pkg-regen
	$(_fakeroot) $(IPKG) -o $(DESTDIR) -f '$<' update || :
	$(_fakeroot) $(IPKG) -o $(DESTDIR) -f '$<' upgrade

.PHONY:		pkg-install
pkg-install:	${STAGING_ETCDIR_NATIVE}/opkg.conf pkg-update
	$(_fakeroot) $(IPKG) -o $(DESTDIR) -f '$<' install ${P}

.PHONY:		pkg-remove
pkg-remove:	${STAGING_ETCDIR_NATIVE}/opkg.conf
	$(_fakeroot) $(IPKG) -o $(DESTDIR) -f '$<' remove ${P}

${_pkgs}:${DEPLOY_DIR_IPK}/%/Packages:	${DEPLOY_DIR_IPK}/%
	$(_ipkg_make_index) -r $@ -p $@ -l $@.filelist -m $<
