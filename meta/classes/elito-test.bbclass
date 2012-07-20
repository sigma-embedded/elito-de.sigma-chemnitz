python do_printdeps() {
    bb.note("PROVIDES: %s -> %s" % (bb.data.getVar('PN', d, True),
                                    " ".join((bb.data.getVar('PROVIDES', d, True) or "").split())))
    bb.note("DEPENDS:  %s -> %s" % (bb.data.getVar('PN', d, True),
                                    " ".join((bb.data.getVar('DEPENDS', d, True) or "").split())))
}

do_printdeps[nostamp] = "1"
addtask printdeps

do_printdepsall() {
	:
}

do_printdepsall[recrdeptask] = "do_printdeps"
addtask printdepsall after do_printdeps
