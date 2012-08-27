SED ?= sed

_sed_cmd = $(SED) \
	-e 's!${STAGING_DIR_TARGET}/\+!\$${T_TARGET}/!g' \
	-e 's!${STAGING_DIR_NATIVE}/\+!\$${T_NATIVE}/!g' \
	-e 's!${STAGING_BINDIR_TOOLCHAIN}/\+!\$${T_CROSS}/!g' \
	-e 's!${_tmpdir}/!\$${T}/!g' \
	-e 's!${_CROSS}!\$${CROSS}!g'

_sed_cmd1 = $(SED) \
	-e 's!${_tmpdir}/!\$${T}/!g' \

_ign_cflags = -f% -O% -g%
_ign_ldflags = -Wl,-O%

ECLIPSE = eclipse
export CROSS = ${_CROSS}
ENV += "CROSS=${CROSS}"

LOCALGOALS =	info run

info:
	echo "Autotools:"
	echo "  Configure Settings"
	echo "    Platform specifiers"
	echo "      Host platform: ${HOST_SYS}"
	echo "      Build platform: ${BUILD_SYS}"
	echo "      Target platform: ${TARGET_SYS}"
	echo "    Advanced"
	echo "      Additional options: PATH=${PATH}" | ${_sed_cmd}
	echo
	echo 'C/C++ Build'
	echo '  Build Variables'
	echo '    CROSS: ${_CROSS}'
	echo '    DESTDIR: ${DESTDIR}'
	echo '    T: ${_tmpdir}'
	echo '    T_CROSS: ${STAGING_BINDIR_TOOLCHAIN}' | ${_sed_cmd1}
	echo '           # ${STAGING_BINDIR_TOOLCHAIN}'
	echo '    T_TARGET: ${STAGING_DIR_TARGET}' | ${_sed_cmd1}
	echo '    T_NATIVE: ${STAGING_DIR_NATIVE}' | ${_sed_cmd1}
	echo '    CC: ${CC}' | ${_sed_cmd}
	echo '    CXX: ${CXX}' | ${_sed_cmd}
	echo '    CFLAGS: ${filter-out ${_ign_cflags},${CFLAGS}}' | ${_sed_cmd}
	echo '    CPPFLAGS: ${filter-out ${_ign_cflags},${CPPFLAGS}}' | ${_sed_cmd}
	echo '    CXXFLAGS: ${filter-out ${_ign_cflags},${CXXFLAGS}}' | ${_sed_cmd}
	echo '    LDFLAGS: ${filter-out ${_ign_ldflags},${LDFLAGS}}' | ${_sed_cmd}

run:
	env -u MAKELEVEL $(ENV) ${ECLIPSE}

MAKEFLAGS += -s
