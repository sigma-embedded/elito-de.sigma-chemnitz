FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRCREV = "c22d359433b333937ee3d803450dc41998115685"
SRC_URI_remove = "\
  file://ltrace-0.7.2-unused-typedef.patch \
  file://0001-In-Linux-backend-initialize-linkmap-struct.patch \
"

SRC_URI += "file://0001-added-long-long-type.patch"
