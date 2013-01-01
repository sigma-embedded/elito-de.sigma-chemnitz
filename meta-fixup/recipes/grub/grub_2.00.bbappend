PATH_prepend = "${S}/.bin:"
CC_prepend = "env COMPILER_PATH=${S}/.bin/gcc "

do_configure_prepend() {
    mkdir -p .bin/gcc
    { echo "#!/bin/sh"
      echo 'exec ${TARGET_PREFIX}ld.bfd "$@"'
    } > .bin/${TARGET_PREFIX}ld
    chmod +x .bin/${TARGET_PREFIX}ld
    ln -s ../${TARGET_PREFIX}ld .bin/gcc/ld
}
