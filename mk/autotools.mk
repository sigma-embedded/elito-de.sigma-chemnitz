CONFIGURE =	./configure
LOCALGOALS =	printcmd configure autoreconf

LIBTOOL_SYSROOT = --with-libtool-sysroot='${STAGING_DIR_HOST}'

_cmdline = \
	--build=${BUILD_SYS} \
	--host=${HOST_SYS} \
	--target=${TARGET_SYS} \
	${LIBTOOL_SYSROOT} \
	PATH='${PATH}' \
	CC='${CC}' \
	CFLAGS='${CFLAGS}' \
	CXX='${CXX}' \
	CXXFLAGS='${CXXFLAGS}' \
	CPP='${CPP}' \
	CPPFLAGS='${CPPFLAGS}' \
	LDFLAGS='${LDFLAGS}'

printcmd:
	@echo "${_cmdline}"

autoreconf:
	autoreconf -i -f ${C}

configure:
	sh ${CONFIGURE} ${_cmdline} ${C}

.PHONY:	configure printcmd

ifeq (${V},)
MAKEFLAGS += " -s"
endif
