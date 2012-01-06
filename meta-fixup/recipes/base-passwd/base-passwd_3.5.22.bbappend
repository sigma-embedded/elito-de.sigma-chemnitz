FILESEXTRAPATHS_prepend := "${THISDIR}:"

SRC_URI += "\
  file://elito-passwd.patch \
  file://passwd-hash.c \
  ${PROJECT_PASSWD_PATCH}"

export DISTRO_ROOTPASSWD

do_compile_append() {
	${BUILD_CC} ${BUILD_CFLAGS} ${BUILD_LDFLAGS} \
		${WORKDIR}/passwd-hash.c -o passwd-hash -lcrypt

	unset h

	if test -n "$DISTRO_ROOTPASSWD"; then
	   h=`./passwd-hash "$DISTRO_ROOTPASSWD" aa`
	   sed -i "s!^\(root\):\([^:]*\):\(.*\)!\1:$h:\3!" passwd.master
	fi
}
