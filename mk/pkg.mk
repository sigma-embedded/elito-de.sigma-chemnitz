OPKG_MAKE_INDEX =	opkg-make-index
FAKEROOT =		${ELITO_TOPDIR}/scripts/run-pseudo '${PROJECT_TOPDIR}'
OPKG =			env PSEUDO_DISABLED=0 opkg-cl
OPKG_ENV =		env D='$(DESTDIR)' LANG=C OPKG_OFFLINE_ROOT='$(DESTDIR)'
OPKG_OPTS =		--force_postinstall

_pkgs =			$(foreach a,${PACKAGE_ARCHS},${DEPLOY_DIR_IPK}/$a/Packages)
_pkgs_dirs =		$(wildcard ${_pkgs})
_pkgs_archs =		$(patsubst ${DEPLOY_DIR_IPK}/%/Packages,%,${_pkgs_dirs})
_pkgs_stamp =		$(foreach a,${_pkgs_archs},${_tmpdir}/stamps/opkg-arch_$a.stamp)

_fakeroot =		$(_start) $(OPKG_ENV) PSEUDO_PASSWD='$(DESTDIR)' $(FAKEROOT)
_opkg_make_index =	$(_start) env LANG=C $(OPKG_MAKE_INDEX)
_opkg_conf =		${DEPLOY_DIR_IPK}/opkg.conf


LOCALGOALS =		pkg-%

${_opkg_conf}:	${_tmpdir}/stamps/opkg.conf.stamp
	@echo "NOTE: creating opkg.conf for: ${_pkgs_archs}"
	${PROJECT_TOPDIR}/bitbake -b /elito-develcomp.bb -c setup_ipkg -f

pkg-regen:	$(wildcard ${_pkgs})
	@:

.PHONY:		pkg-update
pkg-update:	${_opkg_conf} pkg-regen
	$(_fakeroot) $(OPKG) -o $(DESTDIR) -f '$<' $(OPKG_OPTS) update || :

.PHONY:		pkg-upgrade
pkg-upgrade:	${_opkg_conf} pkg-regen
	$(_fakeroot) $(OPKG) -o $(DESTDIR) -f '$<' $(OPKG_OPTS) update || :
	$(_fakeroot) $(OPKG) -o $(DESTDIR) -f '$<' $(OPKG_OPTS) upgrade

.PHONY:		pkg-install
pkg-install:	${_opkg_conf} pkg-update
	$(_fakeroot) $(OPKG) -o $(DESTDIR) -f '$<' $(OPKG_OPTS) install ${P}

.PHONY:		pkg-reinstall
pkg-reinstall:	${_opkg_conf} pkg-update
	$(_fakeroot) $(OPKG) -o $(DESTDIR) -f '$<' $(OPKG_OPTS) --force-reinstall install ${P}

.PHONY:		pkg-remove
pkg-remove:	${_opkg_conf}
	$(_fakeroot) $(OPKG) -o $(DESTDIR) -f '$<' $(OPKG_OPTS) remove ${P}

${_pkgs}:${DEPLOY_DIR_IPK}/%/Packages:	${DEPLOY_DIR_IPK}/%
	$(_opkg_make_index) -r $@ -p $@ -m $<
	test -e $@	   # ensure that target really exists after this rule
	touch $@	   # make sure that timestamps are as expected

${_tmpdir}/stamps/opkg.conf.stamp:	${_pkgs_stamp}
	@touch $@

${_pkgs_stamp}:${_tmpdir}/stamps/opkg-arch_%.stamp:	| ${DEPLOY_DIR_IPK}/%/Packages
	@touch $@
