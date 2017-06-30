ELITO_COMMON_KERNEL_MODULES-can += "\
  kernel-module-flexcan \
"

ELITO_COMMON_KERNEL_MODULES-sound += "\
  kernel-module-snd-soc-fsl-ssi \
  kernel-module-snd-soc-simple-card \
  kernel-module-snd-soc-imx-audmux \
"

ELITO_COMMON_KERNEL_MODULES-usbhost += "\
  kernel-module-phy-mxs-usb \
  kernel-module-ci-hdrc-imx \
  kernel-module-usbmisc-imx \
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

ELITO_COMMON_KERNEL_MODULES-v4l = "\
  kernel-module-imx-ipuv3-csi \
"

ELITO_COMMON_DEPENDS-core += "\
  elito-devicetree-tools-cross-${TARGET_ARCH} \
"
