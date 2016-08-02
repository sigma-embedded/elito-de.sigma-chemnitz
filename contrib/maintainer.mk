GIT = git

all:		split-git-subtrees

split-git-subtrees:	.split-git-subtree-docker

.split-git-subtree-docker:	_prefix=contrib/docker

.split-git-subtree-%:
	$(GIT) subtree split --prefix=$(_prefix) -b $* master
