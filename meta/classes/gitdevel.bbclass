# GIT_REPO   ... (upstream) git repository (mandatory)
# GIT_BRANCH ... git branch (mandatory)
# GIT_ORIGIN_NAME ...  remote name (optional)

GIT_ORIGIN_NAME ?= "origin-elito"
DEPENDS += "git-native"

do_fetchgit() {
	set -x
	install -d ${S}
	cd ${S}

	if test -e ${S}/.git/.dirty; then
		rm -rf ${S}/.git
	fi

	if ! test -d ${S}/.git; then
		git init --shared=group
		touch ${S}/.git/.dirty

		git remote add ${GIT_ORIGIN_NAME} "${GIT_REPO}"
		git config remote.${GIT_ORIGIN_NAME}.fetch 'refs/heads/${GIT_BRANCH}:refs/remotes/${GIT_ORIGIN_NAME}/${GIT_BRANCH}'
		git config remote.${GIT_ORIGIN_NAME}.push  'refs/heads/${GIT_BRANCH}:refs/heads/merge-${PROJECT_NAME}/${GIT_BRANCH}'
		git config remote.${GIT_ORIGIN_NAME}.tagopt --no-tags

		git fetch ${GIT_ORIGIN_NAME}
		git checkout --track -b "${GIT_BRANCH}" "remotes/${GIT_ORIGIN_NAME}/${GIT_BRANCH}"
		git branch -D master || :

		rm -f ${S}/.git/.dirty
	else
		cd ${S}
		git fetch ${GIT_ORIGIN_NAME}
		git pull
	fi
}
addtask fetchgit after do_fetch before do_unpack
