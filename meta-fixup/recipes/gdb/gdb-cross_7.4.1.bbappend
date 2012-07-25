inherit python-dir

EXTRA_OECONF_append = " --with-python=${WORKDIR}/python"

do_configure_prepend() {
cat > ${WORKDIR}/python << EOF
#! /bin/sh
case "\$2" in
        --includes) echo "-I${STAGING_INCDIR_NATIVE}/${PYTHON_DIR}/" ;;
        --ldflags) echo "-Wl,-rpath-link,${STAGING_LIBDIR_NATIVE}/.. -Wl,-rpath,${libdir}/.. -lpthread -ldl -lutil -lm -lpython${PYTHON_BASEVERSION}" ;;
        --exec-prefix) echo "${STAGING_BINDIR_NATIVE}/bin" ;;
        *) exit 1 ;;
esac
exit 0
EOF
        chmod +x ${WORKDIR}/python
}
