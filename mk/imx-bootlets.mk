CFLAGS += -flto
LDFLAGS += -flto -fuse-linker-plugin

IMX_BOOTLET_BOARD ?= iMX28_EVK

OPTS = \
	BOARD=$(IMX_BOOTLET_BOARD) \
	CROSS_COMPILE:='$(_CROSS)' \
	AR:='$(AR)' AS:='$(AS)' CC:='$(CC)' CPP:='$(CPP)' \
	LD:='$(CCLD)' NM:='$(NM)' STRIP:='$(STRIP)' \
	CFLAGS:='${CFLAGS}' \
	LDFLAGS:='${LDFLAGS}' \
	MACHINE:='${MACHINE}' \

unexport
