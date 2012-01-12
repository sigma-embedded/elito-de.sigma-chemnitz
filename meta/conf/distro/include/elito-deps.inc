# --*- python -*--

RRECOMMENDS_libasound_append = " ${@elito_base_switch(d, 'IMAGE_DEV_MANAGER', \
	'udev','udev-sound','')}"

RRECOMMENDS_dropbear_append  = " ${@elito_base_switch(d, 'IMAGE_INIT_MANAGER', \
	'upstart', 'upstart-setup-dropbear', '')}"

RRECOMMENDS_dbus_append = " ${@elito_base_switch(d, 'IMAGE_INIT_MANAGER', \
	'upstart', 'upstart-setup-dbus', '')} \
	elito-dbus-base"

RRECOMMENDS_kernel-module-rt73usb_append = " \
        wireless-tools rt73-firmware	\
        kernel-module-arc4 kernel-module-ecb kernel-module-cryptomgr"

RRECOMMENDS_rt73-firmware_append = " ${FIRMWARE_LOADER_PROVIDER}"

RRECOMMENDS_kernel-module-usb-storage_append = " kernel-module-sd-mod"

RRECOMMENDS_i2c-tools_append		= " kernel-module-i2c-dev"
RRECOMMENDS_udev-rules-base_append	= " udev-rules-ubi"
