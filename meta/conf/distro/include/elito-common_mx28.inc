ELITO_COMMON_KERNEL_MODULES-mmc += "\
  kernel-module-mxs-mmc \
"

ELITO_COMMON_KERNEL_MODULES-can += "\
  kernel-module-flexcan \
"

ELITO_COMMON_KERNEL_MODULES-usbhost += "\
  kernel-module-ehci-mxs \
  kernel-module-mxs-phy \
  kernel-module-phy-mxs-usb \
"

ELITO_COMMON_KERNEL_MODULES-usbclient += "\
  kernel-module-fsl-usb2-udc \
  kernel-module-mxs-phy \
"

ELITO_COMMON_KERNEL_MODULES-screen += "\
  kernel-module-pwm-mxs \
"

ELITO_COMMON_KERNEL_MODULES-restouch += "\
  kernel-module-mxs-lradc \
"

ELITO_COMMON_KERNEL_MODULES-alsa += "\
  kernel-module-snd-soc-mxs \
"

ELITO_COMMON_KERNEL_MODULES-core += "\
  kernel-module-stmp3xxx-rtc-wdt \
"
