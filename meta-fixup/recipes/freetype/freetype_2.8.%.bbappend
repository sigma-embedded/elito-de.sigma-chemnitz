FILESEXTRAPATHS_prepend := "${THISDIR}:"

SRC_URI[vardeps] += "ENTERPRISE_DISTRO"
SRC_URI += " \
  ${@oe.utils.conditional('ENTERPRISE_DISTRO','1','','file://bytecode-interpreter.patch',d)} \
"
