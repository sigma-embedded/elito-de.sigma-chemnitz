PRINC := "${@int('${PRINC}') + 1}"
EXTRA_OECONF_append += " --with-sysroot=${STAGING_DIR_TARGET}"
