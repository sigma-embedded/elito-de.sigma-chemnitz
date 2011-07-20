OPKG_MAKE_INDEX =	opkg-make-index
FAKEROOT =		fakeroot
OPKG =			opkg-cl

_pkgs =			$(foreach a,${PACKAGE_ARCHS},${DEPLOY_DIR_IPK}/$a/Packages)

_fakeroot =		$(_start) $(FAKEROOT)
_opkg_make_index =	$(_start) $(OPKG_MAKE_INDEX)
_opkg_conf =		${DEPLOY_DIR_IPK}/opkg.conf

LOCALGOALS =		pkg-regen pkg-update pkg-upgrade \
			pkg-install pkg-remove

pkg-regen:	$(wildcard ${_pkgs})
	@:

.PHONY:		pkg-update
pkg-update:	${_opkg_conf} pkg-regen
	$(_fakeroot) $(OPKG) -o $(DESTDIR) -f '$<' update || :

.PHONY:		pkg-upgrade
pkg-upgrade:	${_opkg_conf} pkg-regen
	$(_fakeroot) $(OPKG) -o $(DESTDIR) -f '$<' update || :
	$(_fakeroot) $(OPKG) -o $(DESTDIR) -f '$<' upgrade

.PHONY:		pkg-install
pkg-install:	${_opkg_conf} pkg-update
	$(_fakeroot) $(OPKG) -o $(DESTDIR) -f '$<' install ${P}

.PHONY:		pkg-remove
pkg-remove:	${_opkg_conf}
	$(_fakeroot) $(OPKG) -o $(DESTDIR) -f '$<' remove ${P}

${_pkgs}:${DEPLOY_DIR_IPK}/%/Packages:	${DEPLOY_DIR_IPK}/%
	$(_opkg_make_index) -r $@ -p $@ -l $@.filelist -m $<
