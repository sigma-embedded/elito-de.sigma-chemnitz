RDEPENDS_${PN} := "${@' '.join(filter(lambda x: not x.startswith('qemu-'), \
                     (d.getVar('RDEPENDS_${PN}', True) or "").split()))}"
