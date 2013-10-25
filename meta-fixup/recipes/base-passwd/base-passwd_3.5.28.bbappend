FILESEXTRAPATHS_prepend := "${THISDIR}:"

SRC_URI += "\
  file://elito-passwd.patch \
  ${PROJECT_PASSWD_PATCH}"
