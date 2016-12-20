ci-build:	$(foreach p,${PROJECTS},.ci-build-$p)
ci-clean:	$(foreach p,${PROJECTS},.ci-clean-$p)

.ci-build-%:	.ci-prepare-%
	${MAKE} -C '$*' all-images

.ci-clean-%:
	${MAKE} -C '$*' mrproper

.ci-prepare-%:	.ci-top-prepare %/conf/local-local.conf
	${MAKE} configure M=$*

.ci-top-prepare:
	${MAKE} prepare GIT=:
