RDEPENDS_${PN} := "${@' '.join(filter(lambda x: not x.startswith('qemu-'), \
                     (bb.data.getVar('RDEPENDS_${PN}',d,True) or "").split()))}"
