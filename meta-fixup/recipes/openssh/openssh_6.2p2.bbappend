# avoid conflicts in '-c populate_sdk':
#  * check_conflicts_for: The following packages conflict with openssh:
#  * check_conflicts_for:       dropbear *
#  * opkg_install_cmd: Cannot install package openssh-dev.
ALLOW_EMPTY_${PN}-dev = ""
