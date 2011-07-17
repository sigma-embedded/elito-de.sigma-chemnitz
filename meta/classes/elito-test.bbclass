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




python do_find_dups() {
	pass
}

addtask find_dups

python elito_find_dups() {
        name = bb.event.getName(e)
	if name == "CookerNewFiles":
		import os.path
		b = map(lambda x: os.path.basename(x),
			bb.data.getVar('_BBMASKED', e.data, False))

		c = {}
		for l in (bb.data.getVar('ELITO_OLD_CRUFT', e.data, True)
			  or "").split():
			c[l + '.bb'] = 1

		for i in b:
			c.pop(i, None)

		unused = c.keys()
		unused.sort()

		for i in unused:
			print i

		print("|BBMASK| = %u" % len(bb.data.getVar('BBMASK', e.data, True)))
}


addhandler elito_find_dups
