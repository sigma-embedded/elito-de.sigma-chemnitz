class KConfigValue:
    def __init__(self, v):
        self.v = v

YES = KConfigValue('y')
MOD = KConfigValue('m')
OFF = KConfigValue('o')

def sed_cmd(all_options):
    options = []
    for o in all_options:
        if not o[0]:
            continue

        if isinstance(o[1], basestring):
            if len(o) != 3:
                raise Exception("bad number of kernel options: %s" % (o,))
            options.append([o[1], o[2]])
        else:
            options.extend(o[1:])

    disable_opts = filter(lambda x: x[1] == None or  x[1] == False, options)
    enable_opts  = filter(lambda x: x[1] != None and x[1] != False, options)

    interset =  set(map(lambda x: x[0], disable_opts))\
        .intersection(map(lambda x: x[0], enable_opts))

    if interset:
        raise Exception("kernel option conflict: %s" % interset)

    res = []
    new = [ '$a' ]
    pre = [ '1i' ]
    res.append(r's/^\(CONFIG_\(%s\)\)=.*/# \1 is not set/' %
               r'\|'.join(map(lambda x: x[0], disable_opts)))

    res.append(r's/^# \(CONFIG_\(%s\)\) is not set/\1=m/' %
               r'\|'.join(map(lambda x: x[0],
                            filter(lambda x: x[1] == True, enable_opts))))

    for (name, newval) in options:
        if newval in [None, False]:
            pass                        # handled above
        elif newval == True:
            pass                        # handled above
        elif newval == OFF:
            pre.append("# CONFIG_%s is not set" % name)
        elif isinstance(newval, KConfigValue):
            new.append("CONFIG_%s=%s" % (name, newval.v))
        elif isinstance(newval, basestring):
            new.append("CONFIG_%s=\"%s\"" % (name, newval))
        elif isinstance(newval, int):
            new.append("CONFIG_%s=%u" % (name, newval))
        else:
            raise Exception("unsupported value '%s'" % newval)

    res.extend(map(lambda x: x + '\\', pre))
    res.append('')
    res.extend(map(lambda x: x + '\\', new))
    res.append('')

    return '\n'.join(res)

if __name__ == '__main__':
    print(sed_cmd([ [True, 'FOO', None],
                    [True, ['BAR', True], ['B1', 'f']],
                    [True, 'XX', YES],
                    [True, 'XX', YES],
                    [True, 'YY', MOD] ]))
