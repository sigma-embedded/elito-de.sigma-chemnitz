SHELL :=		/bin/bash

INSTALL_DATA =		$(INSTALL) -p -m 0644

ELITO_ROOTDIR :=	$(abspath $(abs_top_srcdir)/..)
ELITO_WORKSPACE_DIR :=	$(abspath $(abs_top_builddir)/../workspace)

_FQDN =			$(shell hostname -f)
_DOMAIN =		$(shell hostname -d)

VPATH ?=		$(abs_top_srcdir)
NOW :=			$(shell date +%Y%m%dT%H%M%S)

CCACHE ?=		ccache
ELITO_CCACHE_SIZE ?=	3G
ELITO_LOGDIR ?=		.log

TARGETS =		elito-image
TARGETS_rescue ?=	elito-rescue-kernel elito-image-stream-native
BO ?=

IMAGEDIR =		$(abspath ${W}/deploy/images)
TARGET_IMAGES =

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
			recipes/helloworld/helloworld.bb \
			recipes/helloworld/files/helloworld.c

PKGS_PREP =		opkg-utils-native	\
			automake-native		\
			pkgconfig-native	\
			libtool-native		\
			gettext-native

ifeq ($(filter release-%,${MAKECMDGOALS}),)
W ?=			tmp
else
W ?=			release-${_gitdate}
export W
endif

ifeq ($(ELITO_OFFLINE),)
else ifeq ($(ELITO_OFFLINE),strict)
else ifeq ($(ELITO_OFFLINE),true)
else
$(error Unsupported value for ELITO_OFFLINE; only <empty>, \
	'strict' and 'true' are supported)
endif

ifeq ($(ELITO_OFFLINE),)
_GITR	=		$(GIT)
else
_GITR	=		:
endif

ifeq ($(ELITO_OFFLINE),strict)
http_proxy =	http://localhost:0
https_proxy =	$(http_proxy)
ftp_proxy =	$(http_proxy)
BB_NO_NETWORK =	1
BB_FETCH_PREMIRRORONLY = 1

export http_proxy https_proxy ftp_proxy BB_NO_NETWORK BB_FETCH_PREMIRRORONLY
endif

.DEFAULT_GOAL :=	config

XTERM_INFO =		_xterm_info

_gitdate =		$(shell $(GIT) show --pretty='format:%ct-%h' | $(SED) '1p;d')
_tmpdir :=		$(abs_top_builddir)/.tmp
_stampdir :=		$(_tmpdir)/stamps
_wstampdir :=		$(_tmpdir)/stamps/$(subst /,_,$W)
_comma :=		,

define _xterm_info
! tty -s || echo -ne "\033]0;OE Build ${PROJECT_NAME}@$${HOSTNAME%%.*}:$${PWD/#$$HOME/~} - `env LANG=C date`$(if $1, - $1)\007"
endef

define _call_cmd
{ \
	( $(call ${XTERM_INFO},$2) ); \
	${_ECHO} $1; \
	$1 && ( $(call ${XTERM_INFO},!DONE!); : ) || ( $(call ${XTERM_INFO},!FAILED!); false ); \
}
endef

_show_help = $(SED) \
-e 's!@'STAMPFILE'@!$@!g' \
-e 's!@'CURDIR'@!$(abspath .)!g' \
"$(abs_top_srcdir)/scripts/$1.help"


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

config:			$(CFG_FILES)

init:			bitbake-fetch | $W/cache/ccache
image release-image:	FORCE bitbake-validate $(_wstampdir)/.prep.stamp inc-build-num

$(TARGET_IMAGES):	$(_wstampdir)/.prep.stamp
$(TARGET_IMAGES) _image image release-image:
			@$(call _call_cmd,$(BITBAKE) $(TARGETS) $(BO),$(TARGETS))

build release-build:	FORCE
ifeq (${R},)
			@{ \
			echo "***" ; \
			echo "*** error: parameter R=<recipe> not set" ; \
			echo "***" ; \
			} >&2
			@false
endif
			@$(call _call_cmd,$(BITBAKE) -b $(R) $(BO),>$(R)<)

pkg-regen pkg-update pkg-upgrade pkg-install pkg-reinstall pkg-remove shell pshell: \
			$(W)/Makefile.develcomp FORCE
			$(SECWRAP_CMD) env ELITO_NO_SECWRAP_CMD=true HISTFILE='${abs_top_builddir}/.bash_history' $(MAKE) -f $< CFG=pkg $@ _secwrap=

$(W)/Makefile.develcomp:
			@{ \
			echo "***" ; \
			echo "*** error: development Makefile not found; please" ; \
			echo "***        build the 'elito-develcomp' package" ; \
			echo "***" ; \
			} >&2
			@false

_fetchenv = env PSEUDO_BUILD=auto $(if $ELITO_FETCH_THREADS,BB_NUMBER_THREADS=${ELITO_FETCH_THREADS})

fetch-all fetchall:	FORCE bitbake-validate init
                        ## call it twice; first step might fail when
                        ## downloading git sources from http mirrors
			@$(call _call_cmd,$(_fetchenv) $(BITBAKE) $(TARGETS) -c fetchall -k,fetching sources) || \
			$(call  _call_cmd,$(_fetchenv) $(BITBAKE) $(TARGETS) -c fetchall -k,fetching sources)

help:			$(abs_top_srcdir)/scripts/make.help
			@cat $<
.PHONY:			help

inc-build-num:		FORCE | $W
			@v=`cat ${W}/build-num 2>/dev/null || echo 0` && \
			echo $$(( v + 1 )) > ${W}/build-num

inc-pr:			FORCE | $W/recipes
ifeq ($(R),)
			@echo "*** Recipename R=<recipe> missing. ***" >&2
			@exit 1
endif
			@echo "PRINC := \"\$${@int('\$${PRINC}') + 1}\"" >> '$W/recipes/$R.bbappend'

taint:			FORCE | $W/recipes
ifeq ($(R),)
			@echo "*** Recipename R=<recipe> missing. ***" >&2
			@exit 1
endif
			touch "$W/recipes/$R.bbappend"
			sed -i -e '/^ELITO_RECIPE_TAINT.*/d' "$W/recipes/$R.bbappend"
			echo "ELITO_RECIPE_TAINT = \"`uuidgen`\"" >> "$W/recipes/$R.bbappend"


sources-tar:
			$(if $L,env L=${L}) ${abs_top_srcdir}/scripts/create-sources-tar $(CACHE_DIR)

###### top level targets }}} ########

###### {{{ image-stream targets #######

ifneq (${IMAGE_STREAM},)
all-images:		TARGETS += ${TARGETS_rescue}
all-images:		image

image-stream:		${IMAGE_STREAM}

${IMAGE_STREAM}:	${IMAGE_STREAM_deps} | $(W)/Makefile.develcomp
			@rm -f $@ $@.tmp
			$(SECWRAP_CMD) env ELITO_NO_SECWRAP_CMD=true $(MAKE) -f $(W)/Makefile.develcomp CFG=image-stream IMAGEDIR="${IMAGEDIR}" ${IMAGE_STREAM_args} image-stream O=$@.tmp _secwrap=
			@mv $@.tmp $@
endif

###### }}} image-stream targets #######


############ {{{ Bitbake and general setup section ##########

_bitbake_srcdir =		$(abs_top_srcdir)/recipes/bitbake/$(BITBAKE_BRANCH)
_bitbake_rev =			$(shell cat $(_bitbake_srcdir)/rev | $(SED) '1p;d')
_bitbake_rev_s =		$(shell cat $(_bitbake_srcdir)/rev | $(SED) '2p;d')
_bitbake_rev_r =		$(shell cat $(_bitbake_srcdir)/rev | $(SED) '3p;d')

_bitbake_root =			$(_tmpdir)/staging
_filesystem-dirs =		$(_stampdir) $(_wstampdir) $W/deploy $(_tmpdir)
_bitbake-dirs =			$(_tmpdir)/bitbake
_bitbake-xtraprogs =		bitbake-layers bitbake-diffsigs bitbake-prserv

_bitbake-bundle =		$(CACHE_DIR)/bitbake-${_bitbake_rev_s}.bundle
_bitbake_setuptools_cached =	$(CACHE_DIR)/$(notdir ${BITBAKE_SETUPTOOLS})
_bitbake_setuptools =		$(_tmpdir)/$(notdir ${BITBAKE_SETUPTOOLS})

.SECONDARY:		$(_bitbake-bundle) $(_bitbake_setuptools_cached)

config:			bitbake-validate
mrproper:		bitbake-clean

bitbake-fetch:		$(_stampdir)/.bitbake.stamp
bitbake-clean:		FORCE
			rm -rf $(_tmpdir)/bitbake $(_bitbake_root)
			rm -f $(_stampdir)/.bitbake.*
			rm -f $(_bitbake-xtraprogs)
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

$(_filesystem-dirs) $(_bitbake-dirs) $(CACHE_DIR) $W $W/recipes $(ELITO_LOGDIR):
			mkdir -p $@

$W/cache/ccache:
			mkdir -p $@
			-env CCACHE_DIR=$@ $(CCACHE) -M $(ELITO_CCACHE_SIZE)

$(_wstampdir)/.prep.stamp:	| bitbake-validate Makefile $(AUTOCONF_FILES) $(_stampdir)/.bitbake.stamp $(_wstampdir)/.pseudo.stamp $W/cache/ccache
			@$(call _call_cmd,$(BITBAKE) $(PKGS_PREP) $(BO),prep)
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
			rm -f ${_bitbake-xtraprogs}
			cd $(_tmpdir)/bitbake && $(PYTHON) setup.py build
			mkdir -p $(_bitbake_root)/lib
			cd $(_tmpdir)/bitbake && \
			env PYTHONPATH=./lib:$(_bitbake_root)/lib$(if ${PYTHONPATH},:,)${PYTHONPATH} $(PYTHON) setup.py install --prefix=$(_bitbake_root) --install-purelib=$(_bitbake_root)/lib -O2
			$(SED) -i -e '/EASY-INSTALL-SCRIPT/aimport os, site; site.addsitedir("$(_bitbake_root)/lib")' $(_bitbake_root)/bin/bitbake
			$(MAKE) bitbake
			p='${_bitbake-xtraprogs}'; for i in $$p; do \
				test -x "${_bitbake_root}/bin/$$i" || continue; \
				ln -s bitbake $$i; \
			done
			@touch $@

$(_tmpdir)/bitbake.env:	$(_stampdir)/.bitbake.install.stamp | $(_tmpdir)
			@rm -f $@ $@.tmp
			@$(call _call_cmd,env PSEUDO_BUILD=auto $(BITBAKE) -e > $@.tmp || { rc=$$?; cat $@.tmp >&2; exit $$rc; })
			@mv $@.tmp $@
.SECONDARY:		$(_tmpdir)/bitbake.env

$(_tmpdir)/pseudo.env:	$(_tmpdir)/bitbake.env $(abs_top_srcdir)/scripts/generate-pseudo-env
			@rm -f $@ $@.tmp
			@$(call _call_cmd,$(abs_top_srcdir)/scripts/generate-pseudo-env '$<' '${abs_top_builddir}' >$@.tmp)
			@mv $@.tmp $@
.SECONDARY:		$(_tmpdir)/pseudo.env

$(_stampdir)/.bitbake.stamp:	$(_stampdir)/.bitbake.install.stamp $(_tmpdir)/pseudo.env
			@touch $@

$(_wstampdir)/.pseudo.stamp:	| $(_wstampdir) $(_stampdir)/.bitbake.stamp $(_tmpdir)/pseudo.env $W $W/cache/ccache
## HACK: allow soft transition from old installations;
## remove me after 2014-01-01
ifneq ($(wildcard $(_stampdir)/.pseudo.stamp),)
			@echo "Old pseudo stamp found; assuming transition from old installation"
			rm -f $(_stampdir)/.pseudo.stamp
else
			@rm -f $@.override
			@test ! -d $W/stamps || { \
				$(call _show_help,make-err-pseudo) >&2; \
				echo; \
				echo -n "Continue build [y|N]? "; \
				read; \
				case $$REPLY in \
				([yYtT1]*) \
					touch $@.override \
					;; \
				(*) \
					exit 1 \
					;; \
				esac \
			}

			@$(call _call_cmd,env PSEUDO_BUILD=auto $(BITBAKE) pseudo-native -c populate_sysroot $(BO),populate_sysroot)
			test -e $@.override || rm -rf $W/stamps
			test -e $@.override || rm -f $W/cache/bb_*
			rm -f $@.override
endif
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
			mkdir -p '$(@D)' '$(@D)/rootfs'
			$(INSTALL_DATA) $(_samples_dir)/Makefile          $(@D)/Makefile
			$(INSTALL_DATA) $(_samples_dir)/.gitignore.sample $(@D)/.gitignore
			touch $@

$(_project_files_file) $(_project_task_file):	$(_project_task_dir)/.stamp
			test -e '$@' || \
			$(SED) -e 's!@'PROJECT_NAME'@!$(PROJECT_NAME)!g' $(abs_top_srcdir)/samples/$(patsubst %-$(PROJECT_NAME).bb,%-project.bb,$(notdir $@)) > '$@'

# make timestamp older to prevent endless runs of this rules; it will
# not be good to do 'touch $@' here because this file is going to be
# edited by the user
			-@test "$<" -ot "$@" || touch --reference "$@" "$<"

############ Sample file generation }}} ###########

############ {{{ 'distribute' rules ##########

PUBLISH_IMAGES ?=
TFTP_IMAGES ?=

define _distribute_do_install
$(INSTALL_DATA) -D '$1' '${D}/$2'
endef

define _distribute__define_image
_distribute_$1_images += $2
_distribute_$1_images-$1: _image-src := $2
_distribute_$1_images-$1: _image-dst := $3
_distribute_$1_image_$2-src = $3
_distribute_$1_image_$2-dst = $3
endef

_distribute_define_image = $$(eval $$(call _distribute__define_image,$1,$2))

_distribute_publish_images =
$(foreach i,$(PUBLISH_IMAGES),\
	$(eval $(call _distribute_define_image,publish,$(subst :,$(_comma),$i))))

_distribute_tftp_images =
$(foreach i,$(TFTP_IMAGES),\
	$(eval $(call _distribute_define_image,tftp,$(subst :,$(_comma),$i))))

_distribute_publish_targets = $(addprefix .publish-images-,$(_distribute_publish_images))
_distribute_tftp_targets = $(addprefix .tftp-images-,$(_distribute_tftp_images))

## {{{ publish-images
ifneq ($(PUBLISH_IMAGES),)
publish-images:	$(_tmpdir)/.versions.txt $(_publish_targets)
	$(INSTALL_DATA) $< $(D)/versions.txt

$(_distribute_publish_targets): .publish-images-%:	$W/deploy/images/% | .publish-check
	$(call _distribute_do_install,$<,$(_distribute_publish_image_$*-dst))
endif
## }}} publish-images


## {{{ tftp-images
ifneq ($(TFTP_IMAGES),)
tftp-images:	D = $(TFTPBOOT_DIR)
tftp-images:	$(_publish_tftp_targets)

$(_publish_tftp_targets): .tftp-images-%:	$W/deploy/images/% | .publish-check
	cat $< > $(D)/${_publish_tftp_image_$*-dst}
endif
## }}} tftp-images


.publish-check:			FORCE
ifeq ($(D),)
	@echo 'D not defined' >&2; false
endif
	@test -d "$(D)" || { echo "No such directory: $(D)" >&2; false; }

$(_tmpdir)/.versions.txt:	FORCE | $(_tmpdir)
	@rm -f $@ $@.tmp
	$(MAKE) -s --no-print-directory -C .. repo-info > $@.tmp
	@mv $@.tmp $@


############ {{{ 'clean' rules ##########
clean:
			rm -f $(filter-out Makefile,${CFG_FILES} ${AUTOCONF_FILES})
			rm -f set-env.in conf/local.conf bitbake
			rm -f config.log Packages.filelist bitbake.lock

ifneq ($(wildcard metrics),)
clean-metrics:		$(ELITO_LOGDIR)/metrics-$(NOW).gz
endif

$(ELITO_LOGDIR)/metrics-%.gz:		metrics | $(ELITO_LOGDIR)
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
			rm -f conf/sanity_info

_sstate_cache_mgmt =	$(ELITO_ROOTDIR)/org.openembedded.core/scripts/sstate-cache-management.sh
_sstate_cache_mgmt_opts =	 --cache-dir='$(W)/sstate-cache' -y -L \
			$(if $(V),--debug)

gc:			FORCE
			bash $(_sstate_cache_mgmt) $(_sstate_cache_mgmt_opts) --remove-duplicated || :
			bash $(_sstate_cache_mgmt) $(_sstate_cache_mgmt_opts) --stamps-dir='$(W)/stamps' || :

############ 'clean' rules }}} ##########


############ {{{ autoconf stuff #########
%:			%.in
			@-rm -f $@.tmp $@
			$(SED) $(SED_EXPR) $< > $@.tmp
			mv $@.tmp $@
			chmod a-w $@

$(AUTOCONF_FILES): %:	config.status %.in
			$(abspath $<)

$(_stampdir)/.config.status.stamp: | $(_stampdir)
			@touch $@

config.status:		$(abs_top_srcdir)/configure | $(_stampdir)/.config.status.stamp
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
