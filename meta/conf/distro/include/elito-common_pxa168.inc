## -*- python -*-

ELITO_COMMON_KERNEL_MODULES-ucb1400 += "\
  kernel-module-snd-pxa2xx-ac97 \
"

ELITO_COMMON_KERNEL_MODULES-alsa += "\
  kernel-module-snd-soc-pxa2xx \
  kernel-module-snd-pxa2xx-ac97 \
"

ELITO_COMMON_KERNEL_MODULES-core += " \
  kernel-module-pxa168-wdt \
  kernel-module-rtc-pxa \
"

ELITO_COMMON_KERNEL_MODULES-usbclient += " \
  kernel-module-mv-udc \
"
