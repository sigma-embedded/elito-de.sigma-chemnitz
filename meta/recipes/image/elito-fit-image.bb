LICENSE = "CLOSED"
DEPENDS += "\
  dtc-native \
  virtual/u-boot-mkimage-native \
  virtual/elito-kernel \
"

PACKAGE_ARCH = "${MACHINE_ARCH}"

FIT_FILES ?= ""
FIT_FILES[type] = "list"

SRC_URI = "\
  ${@' '.join(sorted(map(lambda x: 'file://%s.its' % x, oe.data.typed_value('FIT_FILES', d))))} \
"

fit_image_single() {
	its=$1
	shift

	${CPP} -nostdinc -undef -x assembler-with-cpp "$@" ${WORKDIR}/"${its}.its" -o "${its}.pre-its"
	mkimage -f "${its}.pre-its" "${its}".itb
}

do_build_fitimage() {
	fit_image_single "${FIT_FILE}" ${FIT_OPTS}
}

do_compile[depends] += "virtual/elito-kernel:do_deploy"
python do_compile() {
    l = d.createCopy()
    files = sorted(oe.data.typed_value('FIT_FILES', d))
    for f in files:
        opts = d.getVar('FIT_OPTS_%s' % f, False)

        l.setVar('FIT_FILE', f)
        l.setVar('FIT_OPTS', opts)

        bb.build.exec_func('do_build_fitimage', l)
}

inherit deploy

do_deploy[vardepsexclude] += "DATETIME"
do_deploy[cleandirs] = "${DEPLOYDIR}"
do_deploy() {
	suffix="-${DATETIME}"
	for f in ${FIT_FILES}; do
		install -D -p -m 0644 $f.itb ${DEPLOYDIR}/$f$suffix.itb
		ln -sf $f$suffix.itb ${DEPLOYDIR}/$f.itb
	done
}
addtask deploy before do_build after do_compile
