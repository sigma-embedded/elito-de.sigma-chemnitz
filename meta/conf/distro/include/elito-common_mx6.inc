ELITO_COMMON_KERNEL_MODULES-can += "\
  kernel-module-flexcan \
"

ELITO_COMMON_KERNEL_MODULES-usbhost += "\
  kernel-module-phy-mxs-usb \
  kernel-module-ci-hdrc-imx \
"

ELITO_COMMON_KERNEL_MODULES-core += "\
  kernel-module-imx-thermal \
  kernel-module-rtc-snvs \
"

ELITO_COMMON_KERNEL_MODULES-mmc = "\
  kernel-module-sdhci-esdhc-imx \
"

ELITO_COMMON_KERNEL_MODULES-fb = "\
  kernel-module-parallel-display \
  kernel-module-ci-hdrc-imx \
  kernel-module-imx-ipuv3-crtc \
"

ELITO_COMMON_KERNEL_MODULES-sata = "\
  kernel-module-ahci-imx \
"

ELITO_COMMON_DEPENDS-core += "\
  elito-devicetree-tools-cross \
"
