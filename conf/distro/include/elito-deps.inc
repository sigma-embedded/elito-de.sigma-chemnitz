# --*- python -*--
RRECOMMENDS_libasound_append	= " virtual/snd-device"
RRECOMMENDS_dropbear_append	= " virtual/dropbear-init"
RRECOMMENDS_tinylogin_append	= " virtual/tinylogin-init"
RRECOMMENDS_dbus_append		= " virtual/dbus-init elito-dbus-base"

RRECOMMENDS_kernel-module-rt73usb_append = " \
        wireless-tools rt73-firmware	\
        kernel-module-arc4 kernel-module-ecb kernel-module-cryptomgr"

RRECOMMENDS_rt73-firmware_append = " udev-firmware"

RRECOMMENDS_kernel-module-usb-storage_append = " kernel-module-sd-mod"

RRECOMMENDS_i2c-tools_append		= " kernel-module-i2c-dev"
RRECOMMENDS_udev-rules-base_append	= " udev-rules-ubi"
RRECOMMENDS_mtd-utils-ubi-tools_append	= " \
        mtd-utils-flash-tools mtd-utils-nand-tools"
