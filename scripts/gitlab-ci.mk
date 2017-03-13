CI_CACHEROOT ?= /cache
CI_SOURCEDIR  = ${CI_CACHEROOT}/sources
CI_NFSROOT   ?= $(abspath .)/.nfs

_CI_TOPFLAGS = \
	_MODE=ci \
	CI_NFSROOT='${CI_NFSROOT}' \
	CI_CACHEROOT='${CI_CACHEROOT}' \
	CI_SOURCEDIR='${CI_SOURCEDIR}' \

ci-build ci-clean:
	${MAKE_ORIG} ${_CI_TOPFLAGS} '.$@'

######### {{{ _MODE == ci ########
ifeq (${_MODE},ci)

_ci_target = $(foreach p,${PROJECTS},.ci-$1-$p)
_ci_rule   = $(call _ci_target,$1):.ci-$1-%

.ci-build:	$(call _ci_target,build)
	-${CI_DIR}/source-distribute ${CI_SOURCEDIR}
	-${CI_DIR}/sstate-distribute ${CI_CACHEROOT}/sstate

.ci-clean:	$(call _ci_target,clean)

$(call _ci_rule,image):	.ci-prepare-%
	${MAKE} -C '$*' all-images

$(call _ci_rule,image-stream):	.ci-image-%
	${MAKE} -C '$*' image-stream

$(call _ci_rule,deploy):	.ci-prepare-% .ci-image-%
	${MAKE} -C '$*' ci-deploy CI_DEPLOYDIR="$(abspath .)/_deploy${CI_FLAVOR}"

$(call _ci_rule,build):		.ci-image-% .ci-deploy-%
	:

$(call _ci_rule,clean):
	${MAKE} -C '$*' mrproper

$(call _ci_rule,prepare):	.ci-top-prepare %/conf/local-local.conf
	${MAKE} configure M=$* CACHEROOT='${CI_CACHEROOT}' NFSROOT='${CI_NFSROOT}'

.ci-top-prepare:
	${MAKE} prepare GIT=:

%/conf/local-local.conf:	%/conf/.tmpl.conf
	@rm -f $@
	${CI_DIR}/oe-conf "-" "$(dir ${@D})" "$(abspath $<)" "${@F}" 0

_CI_TMPL_CNF = $(addsuffix /conf/.tmpl.conf,${PROJECTS})

${_CI_TMPL_CNF}:%/conf/.tmpl.conf:	%/conf/.tmpl-50_top.conf
	@rm -f $@
	cat $(sort $^) > $@

%/conf/.tmpl-50_top.conf:
	@rm -f $@
	echo 'DL_DIR = "${CI_SOURCEDIR}"'  > $@

endif
######### }}} _MODE == ci ########
