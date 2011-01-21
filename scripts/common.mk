SHELL :=		/bin/bash

INSTALL_DATA =		$(INSTALL) -p -m 0644

ELITO_ROOTDIR :=	$(abspath $(abs_top_srcdir)/..)
ELITO_WORKSPACE_DIR :=	$(abspath $(abs_top_builddir)/../workspace)

_tmpdir :=		$(abs_top_builddir)/.tmp
_stampdir :=		$(_tmpdir)/stamps

_FQDN =			$(shell hostname -f)
_DOMAIN =		$(shell hostname -d)

VPATH ?=		$(abs_top_srcdir)
W ?=			tmp
NOW :=			$(shell date +%Y%m%dT%H%M%S)

ELITO_SPACE_MIN =	15
ELITO_SPACE_FULL =	30


SED_EXPR =	-e 's!@'ELITO_ROOTDIR'@!$(ELITO_ROOTDIR)!g'	\
		-e 's!@'ELITO_WORKSPACE_DIR'@!$(ELITO_WORKSPACE_DIR)!g' \
		-e 's!@'CACHE_DIR'@!$(CACHE_DIR)!g'		\
		-e 's!@'PYTHON'@!$(PYTHON)!g'			\
		-e 's!@'PROJECT_NAME'@!$(PROJECT_NAME)!g'	\
		-e 's!@'TOP_BUILD_DIR'@!$(abs_top_builddir)!g'	\
		-e 's!@'SECWRAP_CMD'@!$(SECWRAP_CMD)!g'

BITBAKE_REPO =		git://git.openembedded.org/bitbake.git

_bitbake_srcdir =	$(abs_top_srcdir)/recipes/bitbake/$(BITBAKE_BRANCH)
BITBAKE_REV =		$(shell cat $(_bitbake_srcdir)/rev | $(SED) '1p;d')
BITBAKE_REV_S =		$(shell cat $(_bitbake_srcdir)/rev | $(SED) '2p;d')
BITBAKE_REV_R =		$(shell cat $(_bitbake_srcdir)/rev | $(SED) '3p;d')
BITBAKE_SNAPSHOT =	http://www.sigma-chemnitz.de/dl/elito/sources/bitbake-git.tar
BITBAKE_SETUPTOOLS =	http://pypi.python.org/packages/2.6/s/setuptools/setuptools-0.6c9-py2.6.egg

ELITO_STATVFS =		$(PYTHON) ${abs_top_srcdir}/scripts/statvfs

AUTOCONF_FILES =	Makefile		\
			set-env.in		\
			conf/local.conf.in	\
			bitbake

CFG_FILES =		Makefile		\
			set-env			\
			conf/local.conf		\
			bitbake

TEMPLATE_FILES =	conf/project.conf	\
			.gitignore		\
			recipes/helloworld/helloworld.bb

_template_files =	$(addprefix $(abs_top_builddir)/,$(TEMPLATE_FILES))
_project_task_dir =	$(abs_top_builddir)/recipes/$(PROJECT_NAME)
_project_task_file =	$(_project_task_dir)/task-$(PROJECT_NAME).bb
_project_files_file =	$(_project_task_dir)/files-$(PROJECT_NAME).bb
_samples_dir =		$(abs_top_srcdir)/samples

BITBAKE_ENV =		env $(addprefix -u ,\
	MAKEFILES MAKEFLAGS MAKEOVERRIDES MFLAGS VPATH \
	BO TARGETS)

TARGETS =		elito-image
BITBAKE :=		$(BITBAKE_ENV) $(abs_top_builddir)/bitbake
BO ?=
RECIPE ?=

ifeq ($(ELITO_OFFLINE),)
_GITR	=		$(GIT)
else
_GITR	=		:
endif

.DEFAULT_GOAL :=	config

XTERM_INFO =		_xterm_info

-include $(abs_top_builddir)/Makefile.local.$(_FQDN)
-include $(abs_top_builddir)/Makefile.local.$(_DOMAIN)
-include $(abs_top_builddir)/Makefile.local

define _xterm_info
! tty -s || echo -ne "\033]0;OE Build ${PROJECT_NAME}@$${HOSTNAME%%.*}:$${PWD/#$$HOME/~} - `date`$(if $1, - $1)\007"
endef

define _call_cmd
@$(call ${XTERM_INFO},$2)
@${_ECHO} $1
@$1 && ( $(call ${XTERM_INFO},!DONE!); : ) || ( $(call ${XTERM_INFO},!FAILED!); false )
endef

_gitdate = $(shell $(GIT) show --pretty='format:%ct-%h' | $(SED) '1p;d')

release-image release-build:		export W=release-${_gitdate}

config:			$(CFG_FILES) $(_template_files) .gitignore bitbake-validate
config:			$(_project_task_file) $(_project_files_file)

init:			bitbake-fetch filesystem-init
prep:			$(_stampdir)/.prep.stamp Makefile bitbake-validate
image release-image:	prep

_image image release-image:
			$(call _call_cmd,$(BITBAKE) $(TARGETS) $(BO),$(TARGETS))

build release-build:
ifeq (${R},)
			@{ \
			echo "***" ; \
			echo "*** error: parameter R=<recipe> not set" ; \
			echo "***" ; \
			} >&2
			@false
endif
			$(call _call_cmd,$(BITBAKE) -b $(R) $(BO),>$(R)<)

pkg-regen pkg-update pkg-upgrade pkg-install pkg-remove shell: \
			$(W)/Makefile.develcomp
			$(SECWRAP_CMD) env HISTFILE='${abs_top_builddir}/.bash_history' $(MAKE) -f $< CFG=pkg $@ _secwrap=

$(W)/Makefile.develcomp:
			@{ \
			echo "***" ; \
			echo "*** error: development Makefile not found; please" ; \
			echo "***        build the 'elito-develcomp' package" ; \
			echo "***" ; \
			} >&2
			@false

_bitbake_root =		$(_tmpdir)/staging

filesystem-init:	$(_stampdir)/.filesystem.stamp
bitbake-fetch:		$(_stampdir)/.bitbake.stamp
bitbake-clean:
			rm -rf $(_tmpdir)/bitbake $(_bitbake_root)
			rm -f $(_stampdir)/.bitbake.*
ifeq ($(ELITO_OFFLINE),)
			rm -f $(_bitbake-tarball)
endif

bitbake-validate:
			@f=$(_stampdir)/.bitbake.fetch.stamp; \
			test ! -e "$$f" || { \
			v=$$(cat $$f); test x"$$v" = x${BITBAKE_REV}/${BITBAKE_REV_R}; } || { \
			echo "****"; \
			echo "**** BITBAKE revision mismatch; you have '$$v'"; \
			echo "**** but '${BITBAKE_REV}/${BITBAKE_REV_R}' is expected; please execute"; \
			echo "****"; \
			echo "     ( cd '$(abspath .)' && make bitbake-clean && make init )"; \
			echo "****"; \
			exit 1; \
			} >&2

find-dups:		init
			./bitbake -l Collection -d $(TARGETS) -c find_dups

_space_check = ${ELITO_STATVFS} '${abs_top_builddir}/${W}'

$(_stampdir)/.prep.stamp:	$(_stampdir)/.bitbake.stamp $(_stampdir)/.filesystem.stamp
			$(call _call_cmd,$(BITBAKE) stagemanager-native ipkg-utils-native,prep)
			if ! ${_space_check} ${ELITO_SPACE_FULL}; then \
				$(MAKE) _image BO= TARGETS=elito-prep; \
			fi
			@touch $@

$(_stampdir)/.filesystem.stamp:
			@mkdir -p $(@D)
			@touch $@

_bitbake-tarball =	$(CACHE_DIR)/bitbake-${BITBAKE_REV_S}.tar
_bitbake_setuptools =	$(CACHE_DIR)/$(notdir ${BITBAKE_SETUPTOOLS})

.SECONDARY:		$(_bitbake-tarball) $(_bitbake_setuptools)

ifeq ($(ELITO_OFFLINE),)
$(_bitbake-tarball):
			mkdir -p $(@D)
			@rm -f $(_bitbake-tarball).tmp
			$(WGET) $(BITBAKE_SNAPSHOT) -O $(_bitbake-tarball).tmp
			mv -f $(_bitbake-tarball).tmp $(_bitbake-tarball)

$(_bitbake_setuptools):
			mkdir -p $(@D)
			@rm -f $@.tmp
			$(WGET) $(BITBAKE_SETUPTOOLS) -O $@.tmp
			mv -f $@.tmp $@
endif

$(_stampdir)/.bitbake.filesystem.stamp: $(_stampdir)/.filesystem.stamp
			mkdir -p $(_tmpdir)/bitbake
			@touch $@

$(_stampdir)/.bitbake.setuptools.stamp: $(_stampdir)/.bitbake.filesystem.stamp
			$(MAKE) $(_bitbake_setuptools)
			$(INSTALL_DATA) $(_bitbake_setuptools) $(_tmpdir)/bitbake/
			@touch $@

$(_stampdir)/.bitbake.gitinit.stamp: $(_stampdir)/.bitbake.filesystem.stamp
			$(MAKE) $(_bitbake-tarball)
			cd $(_tmpdir)/bitbake && $(GIT) init
			-cd $(_tmpdir)/bitbake && $(GIT) remote add origin ${BITBAKE_REPO}
			-cd $(_tmpdir)/bitbake && $(GIT) config remote.origin.fetch refs/heads/${BITBAKE_BRANCH}:refs/remotes/origin/${BITBAKE_BRANCH}
			-cd $(_tmpdir)/bitbake && $(GIT) config remote.origin.tagopt --no-tags
			-cd $(_tmpdir)/bitbake/.git && $(TAR) xf $(_bitbake-tarball)
			-cd $(_tmpdir)/bitbake/.git/objects/pack && for i in *-elito.pack; do \
				test -e $$i || continue;	\
				echo P $$i >> ../info/packs;	\
			done
			-cd $(_tmpdir)/bitbake && $(GIT) branch ${BITBAKE_BRANCH} ${BITBAKE_REV_S}
			cd $(_tmpdir)/bitbake && { $(_GITR) remote update || $(_GITR) remote update || :; }
			cd $(_tmpdir)/bitbake && $(GIT) branch -M elito elito.old || :
			cd $(_tmpdir)/bitbake && $(GIT) checkout -b elito ${BITBAKE_REV}
			cd $(_tmpdir)/bitbake && $(GIT) reset --hard elito
			cd $(_tmpdir)/bitbake && $(GIT) branch -D elito.old || :
			cd $(_tmpdir)/bitbake && $(GIT) gc
			@touch $@

$(_stampdir)/.bitbake.fetch.stamp: $(_stampdir)/.bitbake.gitinit.stamp $(_stampdir)/.bitbake.setuptools.stamp
			cd $(_tmpdir)/bitbake && { $(_GITR) remote update || $(_GITR) remote update; }
			cd $(_tmpdir)/bitbake && $(GIT) merge ${BITBAKE_REV}
			@echo ${BITBAKE_REV}/${BITBAKE_REV_R} > $@

$(_stampdir)/.bitbake.patch.stamp: $(_stampdir)/.bitbake.fetch.stamp
ifeq ($(QUILT),)
			cd $(_tmpdir)/bitbake && cat $(_bitbake_srcdir)/*.patch | patch -p0
else
			cd $(_tmpdir)/bitbake && $(QUILT) pop -a || :
			rm -rf $(_tmpdir)/bitbake/patches $(_tmpdir)/bitbake/.pc
			mkdir -p $(_tmpdir)/bitbake/patches
			cd $(_tmpdir)/bitbake && $(QUILT) import -p0 $(_bitbake_srcdir)/*.patch
			cd $(_tmpdir)/bitbake && $(QUILT) push -a -f
endif
			@touch $@

$(_stampdir)/.bitbake.stamp:	$(_stampdir)/.bitbake.patch.stamp
			cd $(_tmpdir)/bitbake && $(PYTHON) setup.py build
			mkdir -p $(_bitbake_root)/lib
			cd $(_tmpdir)/bitbake && \
			env PYTHONPATH=./lib:$(_bitbake_root)/lib$(if ${PYTHONPATH},:,)${PYTHONPATH} $(PYTHON) setup.py install --prefix=$(_bitbake_root) --install-purelib=$(_bitbake_root)/lib -O2
			$(SED) -i -e '/EASY-INSTALL-SCRIPT/aimport os, site; site.addsitedir("$(_bitbake_root)/lib")' $(_bitbake_root)/bin/bitbake
			$(MAKE) bitbake
			@touch $@

.gitignore:
			echo "${CFG_FILES}" | xargs -n1 echo > $@

$(_template_files):$(abs_top_builddir)/%:
			test ! -e $@
			mkdir -p $(@D)
			cp --preserve=mode,timestamps ${abs_top_srcdir}/$*.sample $@

$(_project_task_dir)/.stamp:
			mkdir -p '$(@D)' '$(@D)/rootfs' '$(@D)/rootfs/etc/network'
			$(INSTALL_DATA) $(_samples_dir)/Makefile          $(@D)/Makefile
			$(INSTALL_DATA) $(_samples_dir)/.gitignore.sample $(@D)/.gitignore
			$(INSTALL_DATA) $(_samples_dir)/securetty         $(@D)/rootfs/etc/securetty
			$(INSTALL_DATA) $(_samples_dir)/network-interfaces $(@D)/rootfs/etc/network/interfaces
			touch $@

$(_project_files_file) $(_project_task_file):	$(_project_task_dir)/.stamp
			test -e '$@' || \
			$(SED) -e 's!@'PROJECT_NAME'@!$(PROJECT_NAME)!g' $(abs_top_srcdir)/samples/$(patsubst %-$(PROJECT_NAME).bb,%-project.bb,$(notdir $@)) > '$@'

# make timestamp older to prevent endless runs of this rules; it will
# not be good to do 'touch $@' here because this file is going to be
# edited by the user
			-@test "$<" -ot "$@" || touch --reference "$@" "$<"

%:			%.in
			@-rm -f $@.tmp $@
			$(SED) $(SED_EXPR) $< > $@.tmp
			mv $@.tmp $@
			chmod a-w $@

clean:
			rm -f set-env.in conf/local.conf bitbake

ifneq ($(wildcard metrics),)
clean-metrics:		metrics-$(NOW).gz
endif

metrics-%.gz:		metrics
			rm -f $@.tmp
			gzip -c $< > $@.tmp
			mv $@.tmp $@

clean-metrics:
			rm -f metrics

clean-sources:
			rm -f ${CACHE_DIR}/sources/*_svn.*
			rm -f ${CACHE_DIR}/sources/*_hg.*
			rm -f ${CACHE_DIR}/sources/git_*
			rm -f ${CACHE_DIR}/bitbake-*
			rm -f ${CACHE_DIR}/setuptools-*
			rm -f ${CACHE_DIR}/sources/*.md5
			rm -f ${CACHE_DIR}/sources/*.lock
			#find ${CACHE_DIR}/sources -maxdepth 1 -type f -atime +28 -print0 | xargs -0 rm -vf

mrproper:		clean clean-metrics
			rm -rf $W $(_tmpdir)

$(AUTOCONF_FILES): %:	config.status %.in
			$(abspath $<)

config.status:		$(abs_top_srcdir)/configure
			$(abspath $@) --recheck


ifeq ($(filter $(MAKEFLAGS),s),)
_ECHO := echo
else
_ECHO := :
endif

# do not export any variable set here by default
unexport
