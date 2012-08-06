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
