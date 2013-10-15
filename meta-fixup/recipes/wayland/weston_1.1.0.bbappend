FILESEXTRAPATHS_prepend := "${THISDIR}:"

SRC_URI += "file://f66f99d9db2480bb4e58bc3c76e8193dbd4e241a.patch"

EXTRA_PACKAGECONFIG ??= "\
  ${@base_contains('DISTRO_FEATURES', 'screen', '', 'headless', d)} \
  ${@base_contains('DISTRO_FEATURES', 'fb',     'fbdev', '', d)} \
"

PACKAGECONFIG_append = " ${EXTRA_PACKAGECONFIG}"
