## -*- python -*-

ELITO_COMMON_KERNEL_MODULES-usbhost += "\
  kernel-module-ohci-hcd \
"

ELITO_COMMON_KERNEL_MODULES-usbclient += "\
  kernel-module-pxa27x-udc \
"

ELITO_COMMON_KERNEL_MODULES-mmc += "\
  kernel-module-pxamci \
"

ELITO_COMMON_KERNEL_MODULES-pcmcia += "\
  kernel-module-pxa2xx-base \
"

ELITO_COMMON_KERNEL_MODULES-ac97 += "\
  kernel-module-snd-soc-pxa2xx \
  kernel-module-snd-pxa2xx-ac97 \
"

ELITO_COMMON_DIAGNOSIS_TOOLS-core += "\
  pxaregs270 \
  pxaregs320 \
  kernel-module-pxa2xx-wdt \
"
