inherit metadata_scm

PROJECT_BRANCH	    ?= "${@base_get_metadata_git_branch(d.getVar('PROJECT_TOPDIR', True), d)}"
PROJECT_REVISION    ?= "${@base_get_metadata_git_revision(d.getVar('PROJECT_TOPDIR', True), d)}"

PROJECT_BRANCH[unexport] = 1
PROJECT_REVISION[unexport] = 1
