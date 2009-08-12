# --*- python -*--
RRECOMMENDS_append_libasound	= " virtual/snd-device"
RRECOMMENDS_append_dropbear	= " virtual/dropbear-init"
RRECOMMENDS_append_tinylogin	= " virtual/tinylogin-init"
RRECOMMENDS_append_dbus		= " virtual/dbus-init elito-dbus-base"

RRECOMMENDS_append_kernel-module-rt73usb = " \
        wireless-tools rt73-firmware	\
        kernel-module-arc4 kernel-module-ecb kernel-module-cryptomgr"

RRECOMMENDS_append_rt73-firmware = " udev-firmware"

RRECOMMENDS_append_kernel-module-usb-storage = " kernel-module-sd-mod"

RRECOMMENDS_append_i2c-tools		= " kernel-module-i2c-dev"
RRECOMMENDS_append_udev-rules-base	= " udev-rules-ubi"
RRECOMMENDS_append_mtd-utils-ubi-tools	= " \
        mtd-utils-flash-tools mtd-utils-nand-tools"
