FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRCREV = "c22d359433b333937ee3d803450dc41998115685"
SRC_URI = "\
  git://anonscm.debian.org/collab-maint/ltrace.git;branch=master \
  file://0001-added-long-long-type.patch"

# avoid
# .../proc.c:247:3: error: 'readdir_r' is deprecated
CFLAGS += "\
  -Wno-deprecated-declarations \
  -Wno-tautological-compare \
"
