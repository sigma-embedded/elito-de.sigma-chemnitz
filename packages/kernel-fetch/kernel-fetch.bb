DESCRIPTION		 = "Task to fetch ELiTo Linux kernel"
SECTION			 = "base"
LICENSE			 = "GPLv3"
PR			 = "r0"

DEPENDS			 = "git-native socat-native"
do_fetch[depends]	+= "git-native:do_populate_staging"
do_fetch[depends]	+= "socat-native:do_populate_staging"

_gdir = ${ELITO_GIT_MIRROR}/kernel.git


do_fetch() {
	set -x
	test -e ${_gdir}/.broken && rm -rf ${_gdir}
	install -d ${_gdir}
	cd ${_gdir}

	if test ! -e config; then
		touch .broken
		GIT_DIR=. git init --bare
		git remote add --mirror \
			-t "heads/${MACHINE_KERNEL_VERSION}/*" \
			-t "heads/rebase/${MACHINE_KERNEL_VERSION}/*" \
			sigma \
			"${ELITO_GIT_REPO_KERNEL}"
		git config remote.sigma.tagopt --no-tags

		if test -n "${PROJECT_GIT_REPO_KERNEL}"; then
			git remote add --mirror \
			project
			"${PROJECT_GIT_REPO_KERNEL}"
		fi
	fi

	git remote update
	rm -f .broken
}
