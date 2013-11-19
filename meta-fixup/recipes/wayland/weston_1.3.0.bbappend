EXTRA_PACKAGECONFIG ??= "\
  ${@base_contains('DISTRO_FEATURES', 'screen', '', 'headless', d)} \
  ${@base_contains('DISTRO_FEATURES', 'fb',     'fbdev', '', d)} \
"

PACKAGECONFIG_append = " ${EXTRA_PACKAGECONFIG}"

CPPFLAGS += "-I=${includedir}/libdrm"

# TODO: revalidate after 2014-01-01
DEPENDS += "libdrm"
