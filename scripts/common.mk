SHELL :=		/bin/bash

INSTALL_DATA =		$(INSTALL) -p -m 0644

ELITO_ROOTDIR :=	$(abspath $(abs_top_srcdir)/..)
ELITO_WORKSPACE_DIR :=	$(abspath $(abs_top_builddir)/../workspace)

_FQDN =			$(shell hostname -f)
_DOMAIN =		$(shell hostname -d)

VPATH ?=		$(abs_top_srcdir)
W ?=			tmp
NOW :=			$(shell date +%Y%m%dT%H%M%S)

ELITO_SPACE_MIN =	15
ELITO_SPACE_FULL =	30

TARGETS =		elito-image
BO ?=

BITBAKE_REPO =		git://git.openembedded.org/bitbake.git

BITBAKE_SNAPSHOT =	http://www.sigma-chemnitz.de/dl/elito/sources/bitbake-git.bundle
BITBAKE_SETUPTOOLS =	http://pypi.python.org/packages/2.6/s/setuptools/setuptools-0.6c9-py2.6.egg

BITBAKE_ENV =		env $(addprefix -u ,\
	MAKEFILES MAKEFLAGS MAKEOVERRIDES MFLAGS VPATH \
	BO TARGETS)

BITBAKE :=		$(BITBAKE_ENV) $(abs_top_builddir)/bitbake

ELITO_STATVFS =		$(PYTHON) ${abs_top_srcdir}/scripts/statvfs

SED_EXPR =	-e 's!@'ELITO_ROOTDIR'@!$(ELITO_ROOTDIR)!g'	\
		-e 's!@'ELITO_WORKSPACE_DIR'@!$(ELITO_WORKSPACE_DIR)!g' \
		-e 's!@'CACHE_DIR'@!$(CACHE_DIR)!g'		\
		-e 's!@'PYTHON'@!$(PYTHON)!g'			\
		-e 's!@'PROJECT_NAME'@!$(PROJECT_NAME)!g'	\
		-e 's!@'TOP_BUILD_DIR'@!$(abs_top_builddir)!g'	\
		-e 's!@'SECWRAP_CMD'@!$(SECWRAP_CMD)!g'

AUTOCONF_FILES =	Makefile		\
			set-env.in		\
			conf/bblayers.conf.in	\
			conf/local.conf.in	\
			conf/layer.conf		\
			bitbake

CFG_FILES =		Makefile		\
			set-env			\
			conf/bblayers.conf	\
			conf/layer.conf		\
			conf/local.conf		\
			bitbake

TEMPLATE_FILES =	conf/project.conf	\
			.gitignore		\
			recipes/helloworld/helloworld.bb

PKGS_PREP =		opkg-utils-native	\
			automake-native		\
			pkgconfig-native	\
			libtool-native		\
			gettext-native

ifeq ($(ELITO_OFFLINE),)
_GITR	=		$(GIT)
else
_GITR	=		:
endif

.DEFAULT_GOAL :=	config

XTERM_INFO =		_xterm_info

_gitdate =		$(shell $(GIT) show --pretty='format:%ct-%h' | $(SED) '1p;d')
_tmpdir :=		$(abs_top_builddir)/.tmp
_stampdir :=		$(_tmpdir)/stamps

define _xterm_info
! tty -s || echo -ne "\033]0;OE Build ${PROJECT_NAME}@$${HOSTNAME%%.*}:$${PWD/#$$HOME/~} - `env LANG=C date`$(if $1, - $1)\007"
endef

define _call_cmd
@$(call ${XTERM_INFO},$2)
@${_ECHO} $1
@$1 && ( $(call ${XTERM_INFO},!DONE!); : ) || ( $(call ${XTERM_INFO},!FAILED!); false )
endef

define _download
@rm -f '$@.tmp'
$(WGET) '$1' -O '$@.tmp'
mv -f '$@.tmp' '$@'
endef

###### local customizations #######

-include $(abs_top_builddir)/Makefile.local
-include $(abs_top_builddir)/Makefile.local.$(_DOMAIN)
-include $(abs_top_builddir)/Makefile.local.$(_FQDN)

###### {{{ top level targets ########

release-image release-build:		export W=release-${_gitdate}

config:			$(CFG_FILES)

init:			bitbake-fetch
prep:			$(_stampdir)/.prep.stamp Makefile | bitbake-validate
image release-image:	FORCE prep inc-build-num

_image image release-image:
			$(call _call_cmd,$(BITBAKE) $(TARGETS) $(BO),$(TARGETS))

build release-build:	FORCE
ifeq (${R},)
			@{ \
			echo "***" ; \
			echo "*** error: parameter R=<recipe> not set" ; \
			echo "***" ; \
			} >&2
			@false
endif
			$(call _call_cmd,$(BITBAKE) -b $(R) $(BO),>$(R)<)

pkg-regen pkg-update pkg-upgrade pkg-install pkg-reinstall pkg-remove shell: \
			$(W)/Makefile.develcomp FORCE
			$(SECWRAP_CMD) env HISTFILE='${abs_top_builddir}/.bash_history' $(MAKE) -f $< CFG=pkg $@ _secwrap=

find-dups:		FORCE init
			./bitbake -l Collection -c find_dups $(TARGETS)

$(W)/Makefile.develcomp:
			@{ \
			echo "***" ; \
			echo "*** error: development Makefile not found; please" ; \
			echo "***        build the 'elito-develcomp' package" ; \
			echo "***" ; \
			} >&2
			@false

fetch-all fetchall:	FORCE init | bitbake-validate
                        ## call it twice; first step might fail when
                        ## downloading git sources from http mirrors
			$(call _call_cmd,env PSEUDO_BUILD=1 $(BITBAKE) $(TARGETS) -c fetchall -k,fetching sources) || \
			$(call _call_cmd,env PSEUDO_BUILD=1 $(BITBAKE) $(TARGETS) -c fetchall -k,fetching sources)

help:			FORCE $(abs_top_srcdir)/scripts/make.help
			@cat $<

inc-build-num:		FORCE
			@v=`cat ${W}/build-num 2>/dev/null || echo 0` && \
			echo $$(( v + 1 )) > ${W}/build-num

###### top level targets }}} ########


############ {{{ Bitbake and general setup section ##########

_bitbake_srcdir =		$(abs_top_srcdir)/recipes/bitbake/$(BITBAKE_BRANCH)
_bitbake_rev =			$(shell cat $(_bitbake_srcdir)/rev | $(SED) '1p;d')
_bitbake_rev_s =		$(shell cat $(_bitbake_srcdir)/rev | $(SED) '2p;d')
_bitbake_rev_r =		$(shell cat $(_bitbake_srcdir)/rev | $(SED) '3p;d')

_bitbake_root =			$(_tmpdir)/staging
_filesystem-dirs =		$(_stampdir) $W/deploy $(_tmpdir)
_bitbake-dirs =			$(_tmpdir)/bitbake

_bitbake-bundle =		$(CACHE_DIR)/bitbake-${_bitbake_rev_s}.bundle
_bitbake_setuptools_cached =	$(CACHE_DIR)/$(notdir ${BITBAKE_SETUPTOOLS})
_bitbake_setuptools =		$(_tmpdir)/$(notdir ${BITBAKE_SETUPTOOLS})

_space_check =			${ELITO_STATVFS} '${abs_top_builddir}/${W}'

.SECONDARY:		$(_bitbake-bundle) $(_bitbake_setuptools_cached)

config:			bitbake-validate

bitbake-fetch:		$(_stampdir)/.bitbake.stamp
bitbake-clean:		FORCE
			rm -rf $(_tmpdir)/bitbake $(_bitbake_root)
			rm -f $(_stampdir)/.bitbake.*
ifeq ($(ELITO_OFFLINE),)
			rm -f $(_bitbake-bundle)
endif

bitbake-validate:	FORCE | $(_stampdir)/.bitbake.fetch.stamp
			@f=$(_stampdir)/.bitbake.fetch.stamp; \
			test ! -e "$$f" || { \
			v=$$(cat $$f); test x"$$v" = x${_bitbake_rev}/${_bitbake_rev_r}; } || { \
			echo "****"; \
			echo "**** BITBAKE revision mismatch; you have '$$v'"; \
			echo "**** but '${_bitbake_rev}/${_bitbake_rev_r}' is expected; please execute"; \
			echo "****"; \
			echo "     ( cd '$(abspath .)' && make bitbake-clean && make init )"; \
			echo "**** Hint: most Linux terminals allow to copy the line above by a mouse"; \
			echo "**** triple click and to paste it with the middle mouse button"; \
			exit 1; \
			} >&2

$(_filesystem-dirs) $(_bitbake-dirs) $(CACHE_DIR):
			mkdir -p $@

$(_stampdir)/.prep.stamp:	$(_stampdir)/.bitbake.stamp $(_stampdir)/.pseudo.stamp | bitbake-validate
			$(call _call_cmd,$(BITBAKE) $(PKGS_PREP),prep)
			if ! ${_space_check} ${ELITO_SPACE_FULL}; then \
				$(MAKE) _image BO= TARGETS=elito-prep; \
			fi
			@touch $@

ifeq ($(ELITO_OFFLINE),)
$(_bitbake-bundle):	|  $(CACHE_DIR)
			$(call _download,$(BITBAKE_SNAPSHOT))

$(_bitbake_setuptools_cached):	|  $(CACHE_DIR)
			$(call _download,$(BITBAKE_SETUPTOOLS))
endif

$(_bitbake_setuptools):	$(_bitbake_setuptools_cached) | $(_tmpdir)
			$(INSTALL_DATA) $< $@


$(_stampdir)/.bitbake.fetch.stamp: | $(_tmpdir)/bitbake $(_stampdir) $(_bitbake-bundle)
			cd $(_tmpdir)/bitbake && $(GIT) init
			-cd $(_tmpdir)/bitbake && $(GIT) remote rm origin
			-cd $(_tmpdir)/bitbake && $(GIT) branch -M elito _elito
			-cd $(_tmpdir)/bitbake && $(GIT) fetch $(_bitbake-bundle) 'refs/heads/*:refs/remotes/bundle/*'
			-cd $(_tmpdir)/bitbake && $(GIT) remote add origin ${BITBAKE_REPO}
			-cd $(_tmpdir)/bitbake && $(GIT) config remote.origin.fetch refs/heads/${BITBAKE_BRANCH}:refs/remotes/origin/${BITBAKE_BRANCH}
			-cd $(_tmpdir)/bitbake && $(GIT) config remote.origin.tagopt --no-tags
			cd $(_tmpdir)/bitbake && { $(_GITR) remote update || $(_GITR) remote update || :; }
			cd $(_tmpdir)/bitbake && $(GIT) checkout -b elito ${_bitbake_rev}
			cd $(_tmpdir)/bitbake && $(GIT) reset --hard elito
			-cd $(_tmpdir)/bitbake && $(GIT) branch -D _elito
			cd $(_tmpdir)/bitbake && $(GIT) gc
			@echo ${_bitbake_rev}/${_bitbake_rev_r} > $@

$(_stampdir)/.bitbake.patch.stamp: $(_stampdir)/.bitbake.fetch.stamp | $(_tmpdir)/bitbake
ifneq ($(GIT),)
			cd $(_tmpdir)/bitbake && $(GIT) am --reject -3 $(_bitbake_srcdir)/*.patch
else ifeq ($(QUILT),)
			cd $(_tmpdir)/bitbake && cat $(_bitbake_srcdir)/*.patch | patch -p1
else
			cd $(_tmpdir)/bitbake && $(QUILT) pop -a || :
			rm -rf $(_tmpdir)/bitbake/patches $(_tmpdir)/bitbake/.pc
			mkdir -p $(_tmpdir)/bitbake/patches
			cd $(_tmpdir)/bitbake && $(QUILT) import -p1 $(_bitbake_srcdir)/*.patch
			cd $(_tmpdir)/bitbake && $(QUILT) push -a -f
endif
			@touch $@

$(_stampdir)/.bitbake.install.stamp:	$(_stampdir)/.bitbake.patch.stamp | $(_bitbake_setuptools)
			cd $(_tmpdir)/bitbake && $(PYTHON) setup.py build
			mkdir -p $(_bitbake_root)/lib
			cd $(_tmpdir)/bitbake && \
			env PYTHONPATH=./lib:$(_bitbake_root)/lib$(if ${PYTHONPATH},:,)${PYTHONPATH} $(PYTHON) setup.py install --prefix=$(_bitbake_root) --install-purelib=$(_bitbake_root)/lib -O2
			$(SED) -i -e '/EASY-INSTALL-SCRIPT/aimport os, site; site.addsitedir("$(_bitbake_root)/lib")' $(_bitbake_root)/bin/bitbake
			$(MAKE) bitbake
			@touch $@

$(_stampdir)/.bitbake.env.stamp:	$(_stampdir)/.bitbake.install.stamp | $(_tmpdir)
			$(call _call_cmd,env PSEUDO_BUILD=1 $(BITBAKE) -e > $(_tmpdir)/bitbake.env || { rc=$$?; cat $(_tmpdir)/bitbake.env >&2; exit $$rc; })
			@touch $@

$(_stampdir)/.bitbake.stamp:	$(_stampdir)/.bitbake.install.stamp $(_stampdir)/.bitbake.env.stamp
			@touch $@

$(_stampdir)/.pseudo.stamp:	$(_stampdir)/.bitbake.stamp
			$(call _call_cmd,env PSEUDO_BUILD=1 $(BITBAKE) pseudo-native tar-replacement-native,populate_sysroot)
			@touch $@

############ Bitbake and general setup section }}} ##########


############ {{{ Sample file generation ###########

_template_files =	$(addprefix $(abs_top_builddir)/,$(TEMPLATE_FILES))
_project_task_dir =	$(abs_top_builddir)/recipes/$(PROJECT_NAME)
_project_task_file =	$(_project_task_dir)/task-$(PROJECT_NAME).bb
_project_files_file =	$(_project_task_dir)/files-$(PROJECT_NAME).bb
_samples_dir =		$(abs_top_srcdir)/samples

config:			$(_template_files) $(_project_task_file) $(_project_files_file)
config:			.gitignore

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
			touch $@

$(_project_files_file) $(_project_task_file):	$(_project_task_dir)/.stamp
			test -e '$@' || \
			$(SED) -e 's!@'PROJECT_NAME'@!$(PROJECT_NAME)!g' $(abs_top_srcdir)/samples/$(patsubst %-$(PROJECT_NAME).bb,%-project.bb,$(notdir $@)) > '$@'

# make timestamp older to prevent endless runs of this rules; it will
# not be good to do 'touch $@' here because this file is going to be
# edited by the user
			-@test "$<" -ot "$@" || touch --reference "$@" "$<"

############ Sample file generation }}} ###########


############ {{{ 'clean' rules ##########
clean:
			rm -f set-env.in conf/local.conf bitbake

ifneq ($(wildcard metrics),)
clean-metrics:		metrics-$(NOW).gz
endif

metrics-%.gz:		metrics
			rm -f $@.tmp
			$(GZIP) -c $< > $@.tmp
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
############ 'clean' rules }}} ##########


############ {{{ autoconf stuff #########
%:			%.in
			@-rm -f $@.tmp $@
			$(SED) $(SED_EXPR) $< > $@.tmp
			mv $@.tmp $@
			chmod a-w $@

$(AUTOCONF_FILES): %:	config.status %.in
			$(abspath $<)

config.status:		$(abs_top_srcdir)/configure
			$(abspath $@) --recheck

############ autoconf stuff }}} #########

FORCE:
.PHONY:		FORCE

ifeq ($(filter $(MAKEFLAGS),s),)
_ECHO := echo
else
_ECHO := :
endif

# do not export any variable set here by default
unexport
