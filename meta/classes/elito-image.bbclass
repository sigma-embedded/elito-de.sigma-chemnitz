OVERRIDES_append = "${@['',':elito-setdistropasswd']['${DISTRO_ROOTPASSWD}' != '']}"

DEPENDS_elito-setdistropasswd += "elito-build-utils-native"

export DISTRO_ROOTPASSWD

zap_root_password_elito-setdistropasswd() {
    h=`elito-passwd-hash "$DISTRO_ROOTPASSWD"`
    f=${IMAGE_ROOTFS}/etc/shadow
    if test -e "$f"; then
       sed "s%^root:[^:]*:%root:$h:%" "$f" > "$f".new
       rm -f "$f"
       mv "$f".new "$f"
       h='x'
    fi    

    f=${IMAGE_ROOTFS}/etc/passwd
    if test -e "$f"; then
       sed "s%^root:[^:]*:%root:$h:%" "$f" > "$f".new
       rm -f "$f"
       mv "$f".new "$f"
    fi
}

elito_add_devel_history() {
	d=`hostname -d 2>/dev/null` && d=-$d
	h=`hostname -f 2>/dev/null || hostname` && h=-$h

	for p in "$h" "$d" ""; do
		f="${PROJECT_TOPDIR}"/files/bash_history$p
		test -e "$f" || continue
		install -D -p -m 0600 "$f" ${IMAGE_ROOTFS}/root/.bash_history
		break
	done
}

elito_add_devel_sshkey() {
	d=`hostname -d 2>/dev/null` && d=-$d
	h=`hostname -f 2>/dev/null || hostname` && h=-$h

	for p in "$h" "$d" ""; do
		f="${PROJECT_TOPDIR}"/files/authorized_keys$p
		test -e "$f" || continue
		install -D -p -m 0644 "$f" ${IMAGE_ROOTFS}/root/.ssh/authorized_keys
		break
	done
}

ROOTFS_POSTPROCESS_COMMAND += "${@base_contains("IMAGE_FEATURES", "devel-history", \
		                 "elito_add_devel_history", "", d)}"

ROOTFS_POSTPROCESS_COMMAND += "${@base_contains("IMAGE_FEATURES", "devel-sshkey", \
		                 "elito_add_devel_sshkey", "", d)}"
