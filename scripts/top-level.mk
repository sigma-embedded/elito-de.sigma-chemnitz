TAR =		tar
PWD_P =		pwd -P
AUTORECONF =	autoreconf

GIT_TAG_FLAGS =	-a
GIT_TAG_NOW_FMT = %Y%m%dT%H%M%S
GIT_TAG_PREFIX ?=
GIT_SUBMODULE_STRATEGY = --merge

BATCH_CMD ?=

PACK_OPTS =
PACK_API =	1

_submodules = \
	$(shell $(GIT) submodule status --cached | awk '{ print $$2 }')

help:
	@echo -e "\
Usage:  make <op> [M=<module>]\n\
\n\
<op> can be:\n\
    prepare      ...  download submodules and initialize global buildsystem\n\
    update       ...  like 'prepare' but updates existing installation\n\
\n\
    configure M=<module>\n\
    configure-all    ...  calls ./configure for module M or all registered\n\
                          projects; this target will read additional\n\
                          parameters from ./build-setup files in the module\n\
                          directory.\n\
\n\
    reconfigure M=<module>\n\
    reconfigure-all  ...  calls ./config.status --recheck for module M or all\n\
                          registered projects\n\
\n\
    build M=<module>\n\
    build-all        ...  builds module M resp. all registered ones\n\
    build-failed     ...  builds all registered modules which failed during\n\
                          previous 'build-all', 'build-failed' or \n\
                          'build-incomplete' operations\n\
    build-incomplete ...  builds all registered modules which have not been\n\
                          built yet\n\
\n\
"

commit-submodules:
	{ echo "updated submodules"; echo; \
        $(GIT) submodule summary; } \
        | $(GIT) commit -F - ${_submodules}

image:	build

ifneq ($M,)
reconfigure:
	cd $M && cd "`$(PWD_P)`" && ./config.status --recheck

init:
	$(MAKE) -C $M
	$(MAKE) -C $M init

build:
	make -C $M all-images $(EXTRA_BUILD) MAKEFLAGS=$(SUBMAKEFLAGS) MAKEOVERRIDES= $(if $(TARGETS),TARGETS='$(TARGETS)')

clean mrproper gc:
	make -C $M $@

else				# ifneq ($M,)

build:	build-all

endif				# ifneq ($M,)

ifeq ($M,)
_batch_targets = rebuild-all build-all build-incomplete build_failed
$(_batch_targets):	MAKE_ORIG := $(BATCH_CMD) $(MAKE_ORIG) BATCH_CMD=

configure-all:		$(addprefix .configure-,$(PROJECTS))
reconfigure-all:	$(addprefix .reconfigure-,$(PROJECTS))
build-all:		$(addprefix .clean-complete-,$(PROJECTS)) \
			$(addprefix .build-,$(PROJECTS))
clean-all:		$(addprefix .clean-,$(PROJECTS))
mrproper-all:		$(addprefix .mrproper-,$(PROJECTS))
gc-all:			$(addprefix .gc-,$(PROJECTS))
init-all:		$(addprefix .init-,$(PROJECTS))
rebuild-all:		$(addprefix .rebuild-,$(PROJECTS))
build-failed:		$(addprefix .build-failed-,$(PROJECTS))
build-incomplete:	$(addprefix .build-incomplete-,$(PROJECTS))
repo-info:		$(addprefix .repo-info-,$(PUSH_REPOS))
create-changelog:	$(addprefix .create-changelog-,$(PUSH_REPOS))

clone:
	$(MAKE_ORIG) clone SUBMODULES='${_submodules}' _topdir='$(abspath .)' _MODE=clone
update:		prepare
	@touch .stamps/bad-update
	$(GIT) remote update
	$(GIT) pull
	$(GIT) submodule update $(GIT_SUBMODULE_STRATEGY)
	$(if ${ELITO_REPOS},$(MAKE_ORIG) $(addprefix .stamps/elito_fetch-,${ELITO_REPOS}) _MODE=fetch)
	@rm -f .stamps/bad-update
	$(MAKE) .stamps/autoconf-update

update-offline:
	@test -r '$P' || { \
		echo 'Can not read pack $$P' >&2; \
		exit 1; }
	$(abspath ${ELITO_DIR}/scripts/apply-update-pack) '$(abspath $P)'
	$(MAKE_ORIG) .stamps/autoconf-update

create-tag:
	+T=$$(mktemp -t create-tag.XXXXXX) && \
	trap "rm -rf $$T" EXIT && \
	$(MAKE_ORIG) .create-tag T=$$T TAG='$(TAG)'

ifneq ($(GIT_TAG_PREFIX),)
create-tag-now:	TAG := $(GIT_TAG_PREFIX)-$(shell date +$(GIT_TAG_NOW_FMT))
create-tag-now:	create-tag
endif

.reconfigure-%:
	+! test -e "$*"/config.status || $(MAKE_ORIG) reconfigure M=$*

.clean-complete-%:
	@rm -f .succeeded-$*

.build-%:	.clean-complete-% .init-%
	@touch .failed-$*
	@! tty -s || echo -ne "\033]0;OE Build $*@$${HOSTNAME%%.*}:$${PWD/#$$HOME/~} - `env LANG=C date`\007"
	+$(S) $(MAKE_ORIG) .build-target-$*
	@rm -f .failed-$*
	@date > .succeeded-$*

.build-failed-%:
	! test -e .failed-$* || $(MAKE_ORIG) .build-$*

.build-incomplete-%:
	test -e .succeeded-$* || $(MAKE_ORIG) .build-$*

.build-target-%:
	$(MAKE_ORIG) M=$* build

.clean-%:	.clean-complete-%
	rm -rf $*/tmp $*/.tmp .succeeded-$* .failed-$*

.mrproper-%:	.clean-complete-%
	+-test -e $*/Makefile && $(MAKE) -C '$*' mrproper
	rm -rf $*/tmp $*/.tmp .succeeded-$* .failed-$*

.gc-%:
	+-test -e $*/Makefile && $(MAKE) -C '$*' gc

.init-%:
	$(MAKE_ORIG) init M=$*

.rebuild-%:
	$(MAKE_ORIG) .clean-$*
	$(MAKE_ORIG) .init-$*
	$(MAKE_ORIG) .build-$*

CHANGELOG_DIR ?= $(_topdir)

.SECONDARY:	$(addprefix $(abspath $(CHANGELOG_DIR))/CHANGES.,$(PUSH_REPOS))
.create-changelog-%:	$(abspath $(CHANGELOG_DIR))/CHANGES.%
	@:

$(abspath $(CHANGELOG_DIR))/CHANGES.%:	FORCE
ifeq ($(REV),)
	@echo "**** error: REV not set" >&2; exit 1
endif
	cd "$(PUSH_DIR_$*)" && p='$(TAG_PREFIX_$*)' s='$(TAG_SUFFIX_$*)' && $(GIT) whatchanged -M -C $O $(REV) > $@
	test -s $@ || rm -f $@

.repo-info-%:
	@printf '%s (%s):\n' "$*" "$(PUSH_DIR_$*)"
	@b='${PUSH_BRANCHES_$*}'; : $${b:=HEAD}; \
	$(GIT) ls-remote "${PUSH_DIR_$*}" $$b | sed -e 's!^!\t!'

.create-tag:	FORCE | .create-tag-check
	$(MAKE_ORIG) -s --no-print-directory repo-info >$T
	$(GIT) tag $(GIT_TAG_FLAGS) -F $T $(TAG)

.create-tag-check:	FORCE
	@test -n "$(TAG)" || { echo "TAG undefined" >&2; false; }
	@! $(GIT) ls-remote --exit-code . refs/tags/$(TAG) || \
		{ echo "tag '$(TAG)' already defined" >&2; false; }

.stamps/autoconf-update:	$(ELITO_DIR)/configure.ac | .stamps
	rm -f .stamps/autoconf
	$(MAKE_ORIG) prepare
	$(if ${PROJECTS},$(MAKE_ORIG) $(addprefix .reconfigure-,${PROJECTS}))
	@touch $@

.stamps/autoconf: | .stamps
	cd $(ELITO_DIR) && $(AUTORECONF) -i -f
	@touch $@

endif

FORCE:

.PHONY:	image build clean mrproper init update update-offline
.PHONY:	commit-submodules init reconfigure clone
.PHONY:	FORCE

########################################################################################

ifeq (${_MODE},configure)
# special handling of configure: targets which require project dependent
# NFSROOT + CACHEROOT variables


ifeq ($(NFSROOT),)
$(error "NFSROOT not set")
endif

ifeq ($(CACHEROOT),)
$(error "CACHEROOT not set")
endif

_opts = \
	--enable-maintainer-mode	\
	--with-cache-dir='${CACHEROOT}' \
	${CONFIGURE_OPTIONS}

configure:
	cd "`$(PWD_P)`" && env CONFIG_SHELL=/bin/bash /bin/bash ${_topdir}/de.sigma-chemnitz/configure ${_opts} CONFIG_SHELL=/bin/bash PWD_P='$(PWD_P)'

else
.configure-%:	.stamps/autoconf-update
	$(MAKE_ORIG) configure M='$*'

ifneq ($M,)
configure:
	$(MAKE_ORIG) -C '$M' configure _MODE=configure _topdir=$(abspath .)
endif

endif
######################################################################################

ifeq (${_MODE},push)

_generate_pack_prog :=	$(abspath ${ELITO_DIR}/scripts/generate-update-pack)
export GIT

define _build_repo_push

PUSH_BRANCHES_$1 ?= $$(addprefix heads/,$${ELITO_REPO_BRANCHES_$1})
PUSH_TAGS_$1 ?= $${ELITO_REPO_TAGS_$1}
PUSH_PRIO_$1 ?= 00
PUSH_REALDIR_$1 ?=	$${PUSH_DIR_$1}
PACK_OPTS_$1 ?= ${PACK_OPTS}

push:		.push-$1

.push-$1:	$$(foreach r,$$(PUSH_REMOTES_$1) $$(PUSH_REMOTES_COMMON),\
		   ..push+$$r+$1)
..push+%+$1:
	cd $$(PUSH_DIR_$1) && $$(GIT) push $$* $$(foreach b,$${PUSH_BRANCHES_$1},refs/$$b:refs/$$b) $$(PO)

.generate-pack-$1:
	@echo "Packaging repo $1"
	b='$${PUSH_BRANCHES_$1}' p='$(TAG_PREFIX_$1)' s='$(TAG_SUFFIX_$1)' && env \
		BRANCHES="$$$${b:-HEAD}" \
		TAGS='$${PUSH_TAGS_$1}' \
	$(_generate_pack_prog) '$$T/$${PUSH_PRIO_$1}-$1' '$$(abspath $$(PUSH_DIR_$1))' "$R" '$$(PUSH_REALDIR_$1)' $$(PACK_OPTS_$1)
	@echo "======================================="

.generate-pack:	.generate-pack-$1

endef

.generate-pack:
	@echo ${PACK_API} > $T/api
	$(TAR) cf ${_packname} -C $T --owner root --group root --mode go-w,a+rX .

generate-pack:
	T=$$(mktemp -d -t update-pack.XXXXXX) && \
	trap "rm -rf $$T" EXIT && \
	$(MAKE_ORIG) T="$$T" _packname=$(if $P,$P,update-pack-`date +%Y%m%dT%H%M%S`.tar) .$@


$(foreach r,$(PUSH_REPOS),$(eval $(call _build_repo_push,$r)))

else

generate-pack push:
	$(MAKE_ORIG)	$@ _MODE=push

endif				# _MODE == push

###############################################################################

ifeq (${_MODE},create-tag)

ifeq (${TAG},)
$(error TAG not defined)
endif

ifeq (${TAG_OPTS},)
$(error TAG_OPTS not defined)
endif

define _build_repo_create_tag

create-tag-recursive:	.create-tag-repo-$1

.create-tag-repo-$1:
	cd $$(PUSH_DIR_$1) && $$(GIT) tag $(GIT_TAG_FLAGS) $(TAG_OPTS) $(TAG_PREFIX_$1)$(TAG)$(TAG_SUFFIX_$1)

endef

create-tag-recursive:	.create-tag-repo-top
.create-tag-repo-top:	create-tag

$(foreach r,$(filter-out top,$(PUSH_REPOS)),$(eval $(call _build_repo_create_tag,$r)))

else ifeq (${_MODE},)				# create-tag

create-tag-recursive:
	$(MAKE_ORIG) $@ _MODE=create-tag

endif				# create-tag

###############################################################################

ifeq (${_MODE},clone)

ifeq (${DST},)
$(error DST not set)
endif

${DST} ${DST}/workspace:
	mkdir -p $@

.clone-init: | ${DST}
	cd ${DST} && git init

.clone_add-%:	.clone-init
	cd ${DST} && $(GIT) submodule add -- '${_topdir}/$*' '$*'

.clone_special-kernel: | ${DST}/workspace
	$(GIT) clone --mirror=fetch ${_topdir}/workspace/kernel.git ${DST}/workspace/kernel.git

${DST}/Makefile:${DST}/%:	%
	install -p -m 0644 $< $@

clone:	$(addprefix .clone_add-,${SUBMODULES}) ${DST}/Makefile /clone_special-kernel
	cd ${DST} && $(GIT) submodule update --init
	cd ${DST}/de.sigma-chemnitz && autoreconf -i -f

endif
