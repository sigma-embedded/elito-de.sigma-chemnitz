PACKAGECONFIG_append = "\
  libproxy \
  ${@bb.utils.contains('PROJECT_FEATURES', 'alsa', 'alsa', '', d)} \
"

EXTRA_OECONF += "-system-proxies"
