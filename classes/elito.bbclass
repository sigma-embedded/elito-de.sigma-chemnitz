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

def elito_base_switch(d, var_name, *args):
	import bb

	v = bb.data.getVar(var_name, d, 1)
	if len(args) % 2 == 0:
		raise bb.parse.ParseError('elito_base_switch called with odd number of parms: %s' % (args,))

	for i in xrange(0, len(args)-1, 2):
		if v == args[i]:
			return args[i+1]

	return args[-1]

def elito_join_locale(pkgs, langs):
        suffix = map(lambda x: 'locale-%s' % x, langs.split())
        res    = []
        for p in pkgs.split():
		res.append(p)
                res.extend(map(lambda x: '%s-%s' % (p,x), suffix))

	print res
        return ' '.join(res)
