SRC_URI += " \
  ${@base_conditional('ENTERPRISE_DISTRO','1','','file://bytecode-interpreter.patch',d)} \
"
