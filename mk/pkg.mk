IPKG_MAKE_INDEX =	ipkg-make-index
IPKG =			opkg-cl
FAKEROOT =		fakeroot

_pkgs =		$(foreach a,${PACKAGE_ARCHS},${DEPLOY_DIR_IPK}/$a/Packages)

pkg-regen:	$(wildcard ${_pkgs})
	@:

.PHONY:		pkg-update
pkg-update:	${STAGING_ETCDIR_NATIVE}/opkg.conf pkg-regen
	$(FAKEROOT) $(IPKG) -o $(DESTDIR) -f '$<' update || :

.PHONY:		pkg-upgrade
pkg-upgrade:	${STAGING_ETCDIR_NATIVE}/opkg.conf pkg-regen
	$(FAKEROOT) $(IPKG) -o $(DESTDIR) -f '$<' update || :
	$(FAKEROOT) $(IPKG) -o $(DESTDIR) -f '$<' upgrade

.PHONY:		pkg-install
pkg-install:	${STAGING_ETCDIR_NATIVE}/opkg.conf
	$(FAKEROOT) $(IPKG) -o $(DESTDIR) -f '$<' install ${P}

.PHONY:		pkg-remove
pkg-remove:	${STAGING_ETCDIR_NATIVE}/opkg.conf
	$(FAKEROOT) $(IPKG) -o $(DESTDIR) -f '$<' remove ${P}

${_pkgs}:${DEPLOY_DIR_IPK}/%/Packages:	${DEPLOY_DIR_IPK}/%
	$(IPKG_MAKE_INDEX) -r $@ -p $@ -l $@.filelist -m $<
