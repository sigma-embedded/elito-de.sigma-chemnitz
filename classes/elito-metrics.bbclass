# --*- python -*--
addhandler elito_metrics_eventhandler

def elito_metrics_write(d, str):
    import fcntl, bb
    fname = bb.data.getVar('METRICS_FILE', d, True)
    assert(fname != None)

    f = open(fname + ".tmp", "a")
    try:
        fcntl.flock(f.fileno(), fcntl.LOCK_EX)
        f.write(str)
    finally:
        f.close()

def elito_metrics_to_xml(res_a, res_b):
    res = []
    for i in filter(lambda x: x.startswith('ru_'), dir(res_a)):
        try:
            a = res_a.__getattribute__(i)
            b = res_b.__getattribute__(i)

            if 0 and a == 0 and b == 0:
                continue
        except:
            continue

        res.append('<resource id="%s">%s</resource>' % (i[3:], a-b))

    return res


python elito_metrics_eventhandler() {
    import resource, time, fcntl
    from bb import note, error, data
    from bb.event import NotHandled, getName

    if e.data is None or getName(e) == "MsgNote":
        return NotHandled

    if data.getVar("METRICS_FILE", e.data, True) == None:
        return NotHandled


    name = getName(e)
    if name == "PkgStarted":
        assert data.getVar('_PKG_RESOURCES', e.data, False) == None
        assert data.getVar('_PKG_TIME', e.data, False) == None

        data.setVar('_PKG_RESOURCES', resource.getrusage(resource.RUSAGE_CHILDREN), e.data)
        data.setVar('_PKG_TIME',      time.clock(), e.data)
    elif name == "BuildStarted":
        data.setVar('_BUILD_START_CLOCK', time.clock(), e.data)
        data.setVar('_BUILD_START_TIME', time.time(), e.data)
        data.setVar('_BUILD_RESOURCES', resource.getrusage(resource.RUSAGE_CHILDREN), e.data)

        dst_fname = data.getVar("METRICS_FILE", e.data, True)
        try:
            os.unlink(dst_fname + ".tmp")
        except:
            pass
        elito_metrics_write(e.data, '')

    elif name == "BuildCompleted":
        now   = resource.getrusage(resource.RUSAGE_CHILDREN)
        res   = data.getVar('_BUILD_RESOURCES', e.data, False)
        fname = data.getVar('METRICS_FILE', e.data, True)
        f_out = open(fname, 'a')
        f_in  = open(fname + ".tmp", 'r')
        try:
            fcntl.flock(f_out.fileno(), fcntl.LOCK_EX)
            fcntl.fcntl(f_in.fileno(),  fcntl.LOCK_SH)

            f_out.write('<build started="%f" finished="%f" duration="%f">\n' %
                        (data.getVar('_BUILD_START_TIME', e.data, False),
                         time.time(),
                         time.time() - data.getVar('_BUILD_START_TIME', e.data, False)))

            while True:
               buf = f_in.read(8192)
               if len(buf) == 0:
                   break
               f_out.write(buf)

            f_out.write('  <metrics>\n' +
                        ''.join(map(lambda x: '    ' + x + '\n',
                                    elito_metrics_to_xml(now, res))) +
                        "  </metrics>\n")
            f_out.write('</build>\n')
            os.unlink(fname + ".tmp")
        finally:
            f_in.close()
            f_out.close()

    if name == "TaskSucceeded" or name == "TaskFailed":
        res = data.getVar('_PKG_RESOURCES', e.data, False)
        now = resource.getrusage(resource.RUSAGE_CHILDREN)

        assert(res != None)

        info = {
        'now' : time.time(),
        'PV'  : data.getVar('PV', e.data, True),
        'PR'  : data.getVar('PR', e.data, True),
        'PN'  : data.getVar('PN', e.data, True),
        'PF'  : data.getVar('PF', e.data, True),
        'total_time' : time.clock() - data.getVar('_PKG_TIME', e.data, False),
        'result' : ['FAIL','OK'][name == "TaskSucceeded"],
        'task' : e.task }

        x = '  <task name="%(task)s" result="%(result)s" ' \
            'time="%(now)s" pn="%(PN)s" pv="%(PV)s" pr="%(PR)s" duration="%(total_time)f">\n' % info

        elito_metrics_write(e.data,
                            x + ''.join(map(lambda x: '    ' + x + '\n', elito_metrics_to_xml(now, res))) +
                            '  </task>\n')

    return NotHandled
}
