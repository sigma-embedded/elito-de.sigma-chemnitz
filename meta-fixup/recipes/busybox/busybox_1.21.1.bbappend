FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

BUSYBOX_DISABLED_FEATURES ?= "\
  ${@base_contains('DISTRO_FEATURES', 'systemd', 'MDEV', '', d)} \
  RDATE SWAPONOFF IFUPDOWN KLOGD \
"

BUSYBOX_EXTRA_FEATURES ?= "\
  MODINFO INSMOD RMMOD LSMOD MODPROBE DEPMOD \
"

BUSYBOX_FEATURES_COREUTILS = "CP CHMOD CHOWN MKDIR SYNC SLEEP ECHO CHMOD LS RMDIR RM CP CHOWN CAT MV FALSE TOUCH LN MKNOD CHGRP TRUE STTY DATE PWD KILL DD MKDIR UNAME FACTOR FMT LOGNAME CSPLIT DF SEQ TEST TTY PINKY BASE64 TRUNCATE INSTALL BASENAME PASTE CHKSUM RUNCON COMM SORT PR PRINTENV DIRCOLORS OD MD5SUM TR STAT HEAD READLINK ENV SHRED NICE UNIQ WHOAMI ID LINK VDIR UPTIME SHA512SUM TIMEOUT UNEXPAND EXPAND WC CHCON UNLINK GROUPS SHA384SUM TAIL MKTEMP TAC USERS PATHCHK LBRACKET SHUF MKFIFO NL DIRNAME SUM STDBUF JOIN SHA256SUM PTX TSORT SLIT HOSTID CUT PRINTF YES DU TEE SHA224SUM EXPR SHA1SUM FOLD NPROC NOHUP DIR WHO CHROOT"

BUSYBOX_FEATURES_UTIL_LINUX = "DMESG MORE KILL WALL MKSWAP BLOCKDEV PIVOT_ROOT MKFS.MINIX HEXDUMP LAST LOGGER MESG RENICE WALL SETSID CHRT FLOCK HWCLOCK UTMPDUMP EJECT FSCK"

SRC_URI += " \
        file://cvt-color.patch \
	file://nc-shutdown.patch \
\
	file://test-access.patch \
"

def elito_bitbake_generate_del(d):
    f = set(d.getVar("BUSYBOX_DISABLED_FEATURES", True).split())
    return ('\n' +
            r'/^[# ]*CONFIG_\(%s\)[ =]/d' % r'\|'.join(f))

def elito_bitbake_generate_append(d):
    f = set(d.getVar("BUSYBOX_DISABLED_FEATURES", True).split())
    t = set(d.getVar("BUSYBOX_EXTRA_FEATURES", True).split())
    return '\\n'.join([''] +
                      map(lambda x: '# CONFIG_%s is not set' % x, f) +
                      map(lambda x: 'CONFIG_%s=y' % x, t))
                      
OE_DEL += "${@elito_bitbake_generate_del(d)}"
OE_FEATURES += "${@elito_bitbake_generate_append(d)}"

do_install_append() {
    rm -f ${D}${systemd_unitdir}/system/busybox-klogd.service
}
