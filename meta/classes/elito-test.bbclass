python do_printdeps() {
    bb.note("PROVIDES: %s -> %s" % (d.getVar('PN', True),
                                    " ".join((d.getVar('PROVIDES', True) or "").split())))
    bb.note("DEPENDS:  %s -> %s" % (d.getVar('PN', True),
                                    " ".join((d.getVar('DEPENDS', True) or "").split())))
}

do_printdeps[nostamp] = "1"
addtask printdeps

do_printdepsall() {
	:
}

do_printdepsall[recrdeptask] = "do_printdeps"
addtask printdepsall after do_printdeps
