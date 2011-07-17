require libvpx.inc

PR = "${INC_PR}.0"

SRC_URI += "file://libvpx-configure-support-blank-prefix.patch"

SRC_URI[md5sum] = "383f3f07a76099682abb43f79b692b72"
SRC_URI[sha256sum] = "28bd8a8ef216fb570912f0d378668051d99681bf13735b59e68a12ad79f2aa73"

CONFIGUREOPTS += " \
        --prefix=${prefix} \
        --libdir=${libdir} \
"
