XSERVER_DRIVER_mx5 = "xf86-video-imxfb"
XSERVER_DRIVER_mx6 = "xf86-video-imxfb-vivante"
XSERVER = "xserver-xorg \
           xf86-input-evdev \
           ${XSERVER_DRIVER}"

MACHINE_FIRMWARE_mx6q = "firmware-imx-vpu-imx6q"
MACHINE_FIRMWARE_mx6d = "firmware-imx-vpu-imx6d"
MACHINE_FIRMWARE_mx6dl = "firmware-imx-vpu-imx6d"
MACHINE_FIRMWARE_mx53 = "firmware-imx-vpu-imx53 firmware-imx-sdma-imx53"
MACHINE_FIRMWARE_mx51 = "firmware-imx-vpu-imx51 firmware-imx-sdma-imx51"

MACHINE_EXTRA_RRECOMMENDS += "${MACHINE_FIRMWARE}"

MACHINE_GSTREAMER_PLUGIN ?= "gst-fsl-plugin"
MACHINE_GSTREAMER_PLUGIN_mx3 ?= ""

# mx5 GPU libraries
PREFERRED_PROVIDER_virtual/egl_mx5 ?= "amd-gpu-x11-bin-mx51"
PREFERRED_PROVIDER_virtual/libgles1_mx5 ?= "amd-gpu-x11-bin-mx51"
PREFERRED_PROVIDER_virtual/libgles2_mx5 ?= "amd-gpu-x11-bin-mx51"

# mx6 GPU libraries
PREFERRED_PROVIDER_virtual/egl_mx6 ?= "gpu-viv-bin-mx6q"
PREFERRED_PROVIDER_virtual/libgl_mx6 ?= "gpu-viv-bin-mx6q"
PREFERRED_PROVIDER_virtual/libgles1_mx6 ?= "gpu-viv-bin-mx6q"
PREFERRED_PROVIDER_virtual/libgles2_mx6 ?= "gpu-viv-bin-mx6q"
