DESCRIPTION		 = "Task to fetch ELiTo Linux kernel"
SECTION			 = "base"
LICENSE			 = "GPLv3"
PR			 = "r0"

DEPENDS			 = "git-native socat-native"
do_fetch[depends]	+= "git-native:do_populate_staging"
do_fetch[depends]	+= "socat-native:do_populate_staging"
