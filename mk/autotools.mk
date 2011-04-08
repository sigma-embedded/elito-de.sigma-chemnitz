CONFIGURE =	./configure
LOCALGOALS =	printcmd configure

_cmdline = \
	--build=${BUILD_SYS} \
	--host=${HOST_SYS} \
	--target=${TARGET_SYS} \
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

configure:
	sh ${CONFIGURE} ${_cmdline} ${C}

.PHONY:	configure printcmd

ifeq (${V},)
MAKEFLAGS += " -s"
endif
