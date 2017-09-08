_elito_skip := "${@elito_skip(d, None, 'nokernel')}"

OVERRIDES_append = ":kernel-${@(d.getVar('MACHINE_KERNEL_VERSION', \
                                         True) or "").replace('.', '-')}"

_srcrev = "${AUTOREV}"
_srcrev_kernel-3-10 = "8bb495e3f02401ee6f76d1b1d77f3ac9f079e376"
_srcrev_kernel-3-14 = "455c6fdbd219161bd09b1165f11699d6d73de11c"
_srcrev_kernel-3-18 = "b2776bf7149bddd1f4161f14f79520f17fc1d71d"
_srcrev_kernel-4-1  = "b953c0d234bc72e8489d3bf51a276c5c4ec85345"
_srcrev_kernel-4-4  = "afd2ff9b7e1b367172f18ba7f693dfb62bdcb2dc"
_srcrev_kernel-4-5  = "b562e44f507e863c6792946e4e1b1449fbbac85d"
_srcrev_kernel-4-9  = "69973b830859bc6529a7a0468ba0d80ee5117826"
_srcrev_kernel-4-10 = "c470abd4fde40ea6a0846a2beab642a578c0b8cd"
_srcrev_kernel-4-13 = "569dbb88e80deb68974ef6fdd6a13edb9d686261"

SRCREV = "${_srcrev}"
PV     = "${MACHINE_KERNEL_VERSION}"

KERNEL_REPO ??= "git://${ELITO_GIT_WS}/kernel.git;protocol=file"
_branch       = "${MACHINE_KERNEL_VERSION}/kernel.org"
SRC_URI_elito = "${KERNEL_REPO};branch=${_branch}"
SRC_URI[vardepsexclude] += "KERNEL_REPO"

DEFAULT_PREFERENCE = "99"

require ${OECORE_TOPDIR}/meta/recipes-kernel/linux-libc-headers/linux-libc-headers.inc

S            = "${WORKDIR}/git"
