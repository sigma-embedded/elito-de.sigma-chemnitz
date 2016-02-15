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
	u=`id -nu 2>/dev/null` || u=
	d=`hostname -d 2>/dev/null` || d=
	h=`hostname -f 2>/dev/null || hostname` || h=

	set --

	if test -n "$u"; then
		set -- "$@" ${h:+"-$u@$h"} ${d:+"-$u@$d"} "-${u}@"
	fi

	set -- "$@" ${h:+"-$h"} ${d:+"-$d"} ""

	for p in "$@"; do
		f="${PROJECT_TOPDIR}"/files/authorized_keys$p
		test -e "$f" || continue
		install -D -p -m 0644 "$f" ${IMAGE_ROOTFS}${ROOT_HOME}/.ssh/authorized_keys
		break
	done
}

elito_set_rootbash() {
	f=${IMAGE_ROOTFS}/etc/passwd
	if test -x "${IMAGE_ROOTFS}/bin/bash" -a -w "$f"; then
		sed -i -e 's!^\(root:.*:\)/bin/sh$!\1/bin/bash!' "$f"
	fi
}

RM_WORK_EXCLUDE += "${PN}"
do_rm_work() {
    :
}

ROOTFS_POSTPROCESS_COMMAND += "${@base_contains("IMAGE_FEATURES", "devel-history", \
		                 "elito_add_devel_history ;", "", d)}"

ROOTFS_POSTPROCESS_COMMAND += "${@base_contains("IMAGE_FEATURES", "devel-sshkey", \
		                 "elito_add_devel_sshkey ;", "", d)}"

ROOTFS_POSTPROCESS_COMMAND += "${@base_contains("IMAGE_FEATURES", "no-root-bash", \
			         "", "elito_set_rootbash ;", d)}"
