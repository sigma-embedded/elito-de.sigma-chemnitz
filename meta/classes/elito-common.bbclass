## -*- python -*-

OVERRIDES .= ":"

DEPENDS += "elito-develcomp"

def elito_common_expand(v,d):
    implied_features = {
        'ucb1400' : ( 'ac97', ),
        'wm97xx'  : ( 'ac97', ),
        'wm9712'  : ( 'wm97xx', ),
        'wm9715'  : ( 'wm9712', ),
        'captouch' : ( 'touchscreen', ),
        'restouch' : ( 'touchscreen', ),
    }

    machine_features = (d.getVar('MACHINE_FEATURES', True) or "").split()
    distro_features = (d.getVar('DISTRO_FEATURES', True) or "").split()
    blacklist = set((d.getVar('ELITO_COMMON_BLACKLIST', True) or "").split())

    features = set(machine_features + distro_features)
    initsys  = 'initsys-%s' % (d.getVar('IMAGE_INIT_MANAGER', True) or "none")
    features.add(initsys)
    features.add('core')
    cnt = len(features) + 1

    while cnt != len(features):
        cnt = len(features)
        for (base,impl) in implied_features.items():
            if base in features:
                features.update(impl)

    res  = set()
    deps = set(["MACHINE_FEATURES", "DISTRO_FEATURES",
                "ELITO_COMMON_BLACKLIST", "IMAGE_INIT_MANAGER"])
    deps.update((d.getVarFlag(v, 'vardeps', True) or "").split())

    for f in features:
        for var in ['%s-%s' % (v,f), '%s-%s_%s' % (v,f,initsys)]:
            xtra = d.getVar(var, True)
            if xtra:
                deps.add(var)
                res = res.union(xtra.split())

    d.setVarFlag(v, 'vardeps', ' '.join(sorted(deps)))

    return ' '.join(res - blacklist)

######

ELITO_COMMON_BLACKLIST ??= ""

######

ELITO_COMMON_DEPENDS-alsa = "\
  alsa-utils \
"

ELITO_COMMON_DEPENDS-core = "\
  elito-develcomp \
"

ELITO_COMMON_DEPENDS = "${@elito_common_expand('ELITO_COMMON_DEPENDS',d)}"


#####

ELITO_COMMON_KERNEL_MODULES-can = "\
  kernel-module-can-bcm \
  kernel-module-can-raw \
  kernel-module-can-dev \
"

ELITO_COMMON_KERNEL_MODULES-ext2 = "\
  kernel-module-ext4 \
"

ELITO_COMMON_KERNEL_MODULES-initsys-systemd = "\
  kernel-module-binfmt-misc \
"

ELITO_COMMON_KERNEL_MODULES-irda = "\
  kernel-module-sir-dev \
"

ELITO_COMMON_KERNEL_MODULES-mmc = "\
  kernel-module-mmc-block \
"

ELITO_COMMON_KERNEL_MODULES-mtd = "\
  kernel-module-mtdchar \
  kernel-module-mtdblock \
  kernel-module-mtdblock-ro \
"

ELITO_COMMON_KERNEL_MODULES-pcmcia = "\
  kernel-module-ide-gd-mod \
  kernel-module-ide-cs \
"

ELITO_COMMON_KERNEL_MODULES-spi = "\
  kernel-module-spidev \
"

ELITO_COMMON_KERNEL_MODULES-ucb1400 += "\
  kernel-module-ucb1400-core \
  kernel-module-ucb1400-gpio \
  kernel-module-snd-soc-ucb1400 \
  ${@base_contains('MACHINE_FEATURES', 'restouch', 'kernel-module-ucb1400-ts', '', d)} \
"

ELITO_COMMON_KERNEL_MODULES-wm9712 += "\
  kernel-module-snd-soc-wm9712 \
"

ELITO_COMMON_KERNEL_MODULES-captouch += "\
  kernel-module-edt-ft5x06 \
"

ELITO_COMMON_KERNEL_MODULES-usbclient = "\
  kernel-module-g-ether \
  kernel-module-g-serial \
  kernel-module-g-multi \
  kernel-module-g-file-storage \
  kernel-module-g-mass-storage \
  kernel-module-gadgetfs \
"

ELITO_COMMON_KERNEL_MODULES-usbhost = "\
  kernel-module-ehci-hcd \
  kernel-module-ohci-hcd \
  kernel-module-usb-serial \
  kernel-module-usb-storage \
  kernel-module-usbhid \
  kernel-module-ftdi-sio \
  kernel-module-pl2303 \
  kernel-module-usbmon \
"

ELITO_COMMON_KERNEL_MODULES-v4l = "\
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

ELITO_COMMON_KERNEL_MODULES-wifi = "\
  kernel-module-arc4 \
"

ELITO_COMMON_KERNEL_MODULES-screen = "\
  kernel-module-pwm-bl \
"

ELITO_COMMON_KERNEL_MODULES-wm97xx += "\
  ${@base_contains('MACHINE_FEATURES', 'restouch', 'kernel-module-wm97xx-ts', '', d)} \
"

ELITO_COMMON_KERNEL_MODULES-core = "\
  kernel-module-configfs \
  kernel-module-configs \
  kernel-module-evdev \
  kernel-module-gpio-keys \
  kernel-module-gpio-keys-polled \
  kernel-module-hwmon \
  kernel-module-i2c-dev \
  kernel-module-iptable-filter \
  kernel-module-iptable-nat \
  kernel-module-ip-tables \
  kernel-module-loop \
  kernel-module-leds-gpio \
  kernel-module-ledtrig-gpio \
  kernel-module-ledtrig-heartbeat \
  kernel-module-ledtrig-timer \
  kernel-module-ledtrig-default-on \
  kernel-module-gpio-wdt \
"

ELITO_COMMON_KERNEL_MODULES = "${@elito_common_expand('ELITO_COMMON_KERNEL_MODULES',d)}"

#####

ELITO_COMMON_DIAGNOSIS_TOOLS-alsa = "\
  alsa-utils-alsamixer \
  alsa-utils-amixer \
  alsa-utils-aplay \
  alsa-utils-alsactl \
"

ELITO_COMMON_DIAGNOSIS_TOOLS-can = "\
  can-test \
"

ELITO_COMMON_DIAGNOSIS_TOOLS-fb = "\
  elito-fbtest \
"

ELITO_COMMON_DIAGNOSIS_TOOLS-mtd = "\
  mtd-utils \
"

ELITO_COMMON_DIAGNOSIS_TOOLS-screen = "\
  fbset \
"

ELITO_COMMON_DIAGNOSIS_TOOLS-touchscreen = "\
  tslib-calibrate \
  tslib-tests \
"

ELITO_COMMON_DIAGNOSIS_TOOLS-usbhost = "\
  usbutils \
"

ELITO_COMMON_DIAGNOSIS_TOOLS-v4l = "\
  media-ctl \
  v4l-utils \
"

ELITO_COMMON_DIAGNOSIS_TOOLS-core = "\
  strace \
  ltrace \
  lsof \
  i2c-tools \
  tcpdump \
  evtest \
  ldd \
  gdbserver \
  iproute2 \
  devmem2 \
  catchsegv \
  trace-cmd \
"

ELITO_COMMON_DIAGNOSIS_TOOLS = "${@elito_common_expand('ELITO_COMMON_DIAGNOSIS_TOOLS',d)}"

#####

ELITO_COMMON_SERVERS-zeroconf_initsys-systemd = "\
"

ELITO_COMMON_SERVERS-zeroconf = "\
  avahi-daemon \
  avahi-utils \
"

ELITO_COMMON_SERVERS-initsys-systemd = "\
"

ELITO_COMMON_SERVERS-core = "\
  connman \
  connman-plugin-loopback \
  connman-plugin-ethernet \
  ${@base_contains('DISTRO_FEATURES','wifi','connman-plugin-wifi','', d)} \
  connman-tools \
  dropbear \
  openssh-sftp-server \
"

ELITO_COMMON_SERVERS = "${@elito_common_expand('ELITO_COMMON_SERVERS',d)}"

######

ELITO_COMMON_PROGRAMS-ext2 = "\
  e2fsprogs \
  e2fsprogs-e2fsck \
  e2fsprogs-mke2fs \
  e2fsprogs-tune2fs \
  e2fsprogs-badblocks \
"

ELITO_COMMON_PROGRAMS-bluetooth = "\
  bluez4 \
"

ELITO_COMMON_PROGRAMS-hdd = "\
  hdparm \
"

ELITO_COMMON_PROGRAMS-initrd_initsys-systemd = "\
  systemd-remount-rootfs \
"

ELITO_COMMON_PROGRAMS-pam_initsys-systemd = "\
  systemd-logind \
"

ELITO_COMMON_PROGRAMS-mobm320 = "\
  mobm320-tools \
"

ELITO_COMMON_PROGRAMS-nfs = "\
  nfs-utils \
"

ELITO_COMMON_PROGRAMS-pci = "\
  pciutils \
  pciutils-ids \
"

ELITO_COMMON_PROGRAMS-touchscreen = "\
  pointercal \
"

ELITO_COMMON_PROGRAMS-v4l = "\
  gst-plugins-bad-fbdevsink \
  gst-plugins-base-ffmpegcolorspace \
  gst-plugins-good-video4linux2 \
  gst-plugins-base-typefindfunctions \
  gst-plugins-base-apps \
"

ELITO_COMMON_PROGRAMS-wifi = "\
  wireless-tools \
  wpa-supplicant \
"

ELITO_COMMON_PROGRAMS-x11 = "\
  xhost \
  xrandr \
  xset \
  xorg-minimal-fonts \
  xserver-xorg \
  xserver-common \
  mesa \
"

ELITO_COMMON_PROGRAMS-core = "\
  tzdata \
  tzdata-europe \
"

ELITO_COMMON_PROGRAMS = "${@elito_common_expand('ELITO_COMMON_PROGRAMS',d)}"

include ${ELITO_COMMON_MACHINE_CONFIGURATION}
