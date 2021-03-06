PATH_prepend = "${B}/.bin:"
CC_prepend = "env COMPILER_PATH=${B}/.bin/gcc "

do_configure_prepend() {
    mkdir -p ${B}/.bin/gcc
    { echo "#!/bin/sh"
      echo 'exec ${TARGET_PREFIX}ld.bfd "$@"'
    } > ${B}/.bin/${TARGET_PREFIX}ld
    chmod +x ${B}/.bin/${TARGET_PREFIX}ld
    ln -s ../${TARGET_PREFIX}ld ${B}/.bin/gcc/ld
}
