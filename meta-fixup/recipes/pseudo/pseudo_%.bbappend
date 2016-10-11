FILESEXTRAPATHS_prepend := "${THISDIR}:"

def detect_selinux(d):
    # crude hack; this sholud be done in toplevel configure.ac but
    # because patchset has been removed in recent ELiTo releases, it
    # is not worth the effort...
    prog = [ '/sbin/selinuxenabled', '/usr/sbin/selinuxenabled',
             '/bin/selinuxenabled', '/usr/bin/selinuxenabled']
    ret = 1

    for p in prog:
        if not os.path.exists(p):
            continue

        ret = os.spawnve(os.P_WAIT, p, [p,], { "PSEUDO_DISABLED" : "1" })
        break

    if ret == 0:
        return "true"
    else:
        return "false"

PSEUDO_SELINUX_PATCH ?= "${@detect_selinux(d)}"
PSEUDO_SELINUX_PATCH[type] = "boolean"

SRC_URI += "\
    ${@['', 'file://0001-added-is_selinux_enabled-wrapper.patch'][oe.data.typed_value('PSEUDO_SELINUX_PATCH', d)]} \
"
