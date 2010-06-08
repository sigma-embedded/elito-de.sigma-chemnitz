_xflags = -fomit-frame-pointer -fpermissive
CFLAGS := $(filter-out ${_xflags},${CFLAGS}) -D_FORTIFY_SOURCE=2 -g3
CXXFLAGS := $(filter-out ${_xflags},${CXXFLAGS}) -D_FORTIFY_SOURCE=2 -g3
