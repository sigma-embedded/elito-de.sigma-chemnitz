OPTS = \
  LIBDIR=. \
  VDRDIR=${STAGING_LIBDIR}/vdr \
  STRIP=/bin/true \
  CXXFLAGS='${CXXFLAGS} -fPIC' \
  CFLAGS='${CFLAGS} -fPIC' \
