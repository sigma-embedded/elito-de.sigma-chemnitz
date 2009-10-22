# --*- python -*--
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

def elito_skip(d, feature_pos, feature_neg = None, var_name = "MACHINE_FEATURES"):
	import bb

	v  = bb.data.getVar(var_name, d, 1).split(' ')
	pn = bb.data.getVar('PN', d, 1)

	if ((feature_pos != None and feature_pos not in v) or
	    (feature_neg != None and feature_neg     in v)):
		raise bb.parse.SkipPackage('Package %s not available for this machine' % pn)
	if (feature_pos == None and feature_neg == None):
		raise bb.parse.ParseError('No feature given in package %s' % pn)
