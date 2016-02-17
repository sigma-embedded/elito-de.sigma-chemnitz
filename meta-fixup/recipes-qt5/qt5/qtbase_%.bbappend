require fixup-pkgconfig.inc

PACKAGECONFIG_append = "\
  libproxy \
  ${@base_contains('PROJECT_FEATURES', 'alsa', 'alsa', '', d)} \
"

EXTRA_OECONF += "-system-proxies"
