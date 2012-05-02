## -*- python -*-

OVERRIDES .= ":"

DEPENDS += "elito-develcomp"

def elito_common_expand(v,d):
  machine_features = (d.getVar('MACHINE_FEATURES', True) or "").split()
  distro_features = (d.getVar('DISTRO_FEATURES', True) or "").split()

  features = set(machine_features + distro_features)
  initsys  = 'initsys-%s' % (d.getVar('IMAGE_INIT_MANAGER', True) or "none")
  features.add(initsys)
  features.add('core')

  res = set()
  for f in features:
    xtra = d.getVar('%s-%s' % (v,f), False)
    if xtra:
      res.add(xtra)

    xtra = d.getVar('%s-%s_%s' % (v,f,initsys), False)
    if xtra:
      res.add(xtra)

  return ' '.join(res)

######

ELITO_COMMON_DEPENDS-alsa = "\
  alsa-utils \
"

ELITO_COMMON_DEPENDS-core = "\
  elito-develcomp \
"

ELITO_COMMON_DEPENDS = "${@elito_common_expand('ELITO_COMMON_DEPENDS',d)}"


#####

ELITO_COMMON_KERNEL_MODULES-usbhost = "\
  kernel-module-ehci-hcd \
  kernel-module-usb-serial \
  kernel-module-usb-storage \
  kernel-module-usbhid \
  kernel-module-ftdi-sio \
  kernel-module-pl2303 \
  kernel-module-usbmon \
"

ELITO_COMMON_KERNEL_MODULES-usbclient = "\
  kernel-module-g-ether \
  kernel-module-g-serial \
  kernel-module-g-multi \
  kernel-module-g-file-storage \
  kernel-module-g-mass-storage \
  kernel-module-gadgetfs \
"

ELITO_COMMON_KERNEL_MODULES-vfat = "\
  kernel-module-nls-ascii \
  kernel-module-nls-cp437 \
  kernel-module-nls-cp850 \
  kernel-module-nls-iso8859-1 \
  kernel-module-nls-iso8859-15 \
  kernel-module-nls-utf8 \
  kernel-module-vfat \
"

ELITO_COMMON_KERNEL_MODULES-mtd = "\
  kernel-module-mtdchar \
  kernel-module-mtdblock \
  kernel-module-mtdblock-ro \
"

ELITO_COMMON_KERNEL_MODULES-can = "\
  kernel-module-can-bcm \
  kernel-module-can-raw \
  kernel-module-can-dev \
"

ELITO_COMMON_KERNEL_MODULES-alsa = "\
"

ELITO_COMMON_KERNEL_MODULES-ext2 = "\
  kernel-module-ext4 \
"

ELITO_COMMON_KERNEL_MODULES-mmc = "\
  kernel-module-mmc-block \
"

ELITO_COMMON_KERNEL_MODULES-pcmcia = "\
  kernel-module-ide-gd-mod	\
  kernel-module-ide-cs		\
"

ELITO_COMMON_KERNEL_MODULES-initsys-systemd = "\
  kernel-module-binfmt-misc \
"

ELITO_COMMON_KERNEL_MODULES-irda = "\
  kernel-module-sir-dev		\
"

ELITO_COMMON_KERNEL_MODULES-v4l = "\
"

ELITO_COMMON_KERNEL_MODULES-wifi = "\
  kernel-module-arc4 \
"

ELITO_COMMON_KERNEL_MODULES-core = "\
  kernel-module-configfs		\
  kernel-module-configs		\
  kernel-module-evdev \
  kernel-module-gpio-keys \
  kernel-module-hwmon \
  kernel-module-i2c-dev \
  kernel-module-iptable-filter \
  kernel-module-iptable-nat \
  kernel-module-loop \
  kernel-module-leds-gpio \
  kernel-module-ledtrig-gpio \
  kernel-module-ledtrig-heartbeat \
  kernel-module-ledtrig-timer \
  kernel-module-ledtrig-default-on \
"

ELITO_COMMON_KERNEL_MODULES = "${@elito_common_expand('ELITO_COMMON_KERNEL_MODULES',d)}"

#####

ELITO_COMMON_DIAGNOSIS_TOOLS-touchscreen = "\
  tslib-calibrate \
  tslib-tests \
"

ELITO_COMMON_DIAGNOSIS_TOOLS-screen = "\
  elito-fbtest \
  fbset \
"

ELITO_COMMON_DIAGNOSIS_TOOLS-usbhost = "\
  usbutils \
"

ELITO_COMMON_DIAGNOSIS_TOOLS-can = "\
  can-test \
"

ELITO_COMMON_DIAGNOSIS_TOOLS-mtd = "\
  mtd-utils \
"

ELITO_COMMON_DIAGNOSIS_TOOLS-alsa = "\
  alsa-utils-alsamixer \
  alsa-utils-amixer \
  alsa-utils-aplay \
  alsa-utils-alsactl \
"

ELITO_COMMON_DIAGNOSIS_TOOLS-v4l = "\
  media-ctl \
  v4l-utils \
"

ELITO_COMMON_KERNEL_MODULES-ucb1400 += "\
  kernel-module-ucb1400-core	\
  kernel-module-ucb1400-gpio	\
  kernel-module-snd-soc-ucb1400 \
  ${@base_contains('MACHINE_FEATURES', 'touchscreen', 'kernel-module-ucb1400-ts', '', d)} \
"

ELITO_COMMON_KERNEL_MODULES-wm97xx += "\
  ${@base_contains('MACHINE_FEATURES', 'touchscreen', 'kernel-module-wm97xx_ts', '', d)} \
"

ELITO_COMMON_DIAGNOSIS_TOOLS-core = "\
  strace \
  ltrace \
  lsof \
  i2c-tools \
  tcpdump \
  minicom \
  evtest \
  ldd \
  gdbserver \
  iproute2 \
  devmem2 \
  catchsegv \
"

ELITO_COMMON_DIAGNOSIS_TOOLS = "${@elito_common_expand('ELITO_COMMON_DIAGNOSIS_TOOLS',d)}"

#####

ELITO_COMMON_SERVERS-zeroconf_initsys-systemd = "\
  avahi-systemd \
"

ELITO_COMMON_SERVERS-zeroconf = "\
  avahi-daemon \
  avahi-utils \
"

ELITO_COMMON_SERVERS-initsys-systemd = "\
  connman-systemd \
  dropbear-systemd \
"

ELITO_COMMON_SERVERS-core = "\
  connman \
  connman-plugin-loopback \
  connman-plugin-ethernet \
  ${@base_contains('DISTRO_FEATURES','wifi','connman-plugin-wifi','', d)} \
  connman-tools \
  dropbear \
"

ELITO_COMMON_SERVERS = "${@elito_common_expand('ELITO_COMMON_SERVERS',d)}"

######

ELITO_COMMON_PROGRAMS-v4l = "\
  gst-plugins-bad-fbdevsink	\
  gst-plugins-base-ffmpegcolorspace \
  gst-plugins-good-video4linux2	\
  gst-plugins-base-typefindfunctions \
  gst-plugins-base-apps		\
"

ELITO_COMMON_PROGRAMS-wifi = "\
  wireless-tools			\
  wpa-supplicant			\
"

ELITO_COMMON_PROGRAMS-mobm320 = "\
  mobm320-tools \
"

ELITO_COMMON_PROGRAMS-core = "\
  opkg \
  tzdata \
  tzdata-europe \
"

ELITO_COMMON_PROGRAMS = "${@elito_common_expand('ELITO_COMMON_PROGRAMS',d)}"

include ${ELITO_COMMON_MACHINE_CONFIGURATION}
