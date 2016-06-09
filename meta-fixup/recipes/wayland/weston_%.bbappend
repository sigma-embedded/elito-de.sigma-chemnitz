EXTRA_PACKAGECONFIG ??= "\
  ${@bb.utils.contains('DISTRO_FEATURES', 'screen', '', 'headless', d)} \
  ${@bb.utils.contains('DISTRO_FEATURES', 'fb',     'fbdev', '', d)} \
"

PACKAGECONFIG_append = " ${EXTRA_PACKAGECONFIG}"


# TODO: revalidate after 2014-01-01; last checked: 2013-12-01 (1.3.1)
#CPPFLAGS += "-I=${includedir}/libdrm"
#DEPENDS += "libdrm"
