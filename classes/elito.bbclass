def get_filedate(base_var, file, d):
	import bb, os, time
	base = bb.data.getVar(base_var,d)
	full = os.path.join(base, file)

	try:
		st = os.stat(full)
	except:
		st = None

	if st == None:
		raise Exception("can not stat file")

	tm = time.gmtime(st[8])
	return '%04d%02d%02d%02d%02d%02d' % (tm[0], tm[1], tm[2], tm[3], tm[4], tm[5])
