ELITO_COMMON_KERNEL_MODULES-can += "\
  kernel-module-flexcan \
"

ELITO_COMMON_KERNEL_MODULES-usbhost += "\
  kernel-module-phy-mxs-usb \
"

ELITO_COMMON_KERNEL_MODULES-core += "\
  kernel-module-imx-thermal \
"

ELITO_COMMON_KERNEL_MODULES-mmc = "\
  kernel-module-sdhci-esdhc-imx \
"

ELITO_COMMON_KERNEL_MODULES-fb = "\
  kernel-module-parallel-display \
  kernel-module-ci_hdrc_imx \
  kernel-module-imx-ipuv3-crtc \
"
