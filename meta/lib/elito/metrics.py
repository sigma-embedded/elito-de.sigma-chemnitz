import bb
import time, fcntl, os

def _metrics_id(d):
    res = d.getVar('METRICS_ID', True)
    assert(res != None)

    return res

def _metrics_tmpname(d):
    import os.path

    fname = d.getVar('METRICS_FILE', True)
    tmpd  = d.getVar('TMPDIR', True)
    mid   = _metrics_id(d)

    assert(fname != None)
    assert(tmpd != None)
    assert(mid != None)

    return os.path.join(tmpd,
                        os.path.basename(fname) + ".tmp.%s" % mid)

def __write(d, str):
    f = open(_metrics_tmpname(d), "a")
    try:
        fcntl.flock(f.fileno(), fcntl.LOCK_EX)
        f.write(str)
    finally:
        f.close()

def __to_xml(who, res_a, res_b):
    res = []
    for i in filter(lambda x: x.startswith('ru_'), dir(res_a)):
        try:
            a = res_a.__getattribute__(i)
            b = res_b.__getattribute__(i)

            if 0 and a == 0 and b == 0:
                continue
        except:
            continue

        res.append('<resource who="%s" id="%s">%s</resource>' % (who, i[3:], a-b))

    return res

def __write_build_complete(e, start_info, end_info, fname):
    f_in_name = _metrics_tmpname(e.data)

    try:
        f_out = open(fname, 'a')
        f_in  = open(f_in_name, 'r')
        os.unlink(f_in_name)
    except:
        bb.warn("Failed to execute build-complete step")
        return

    try:
        fcntl.flock(f_out.fileno(), fcntl.LOCK_EX)
        fcntl.fcntl(f_in.fileno(),  fcntl.LOCK_SH)

        f_out.write('<!-- Started: %s   Ended: %s -*- xml -*- -->\n' %
                    (time.strftime('%c', time.gmtime(start_info['time'])),
                     time.strftime('%c', time.gmtime(end_info['time']))))

        f_out.write('<build project="%s" tmpdir="%s" started="%f" finished="%f"' \
                        ' duration="%f" id="%s">\n' %
                    (e.data.getVar('PROJECT_NAME', True),
                     os.path.basename(e.data.getVar('TMPDIR', True)),
                     start_info['time'], end_info['time'],
                     end_info['time'] - start_info['time'],
                     _metrics_id(e.data)))

        f_out.write('  <sysinfo>\n')

        uname = os.uname()
        f_out.write('    <hostname>%s</hostname>\n' \
                        '    <release>%s</release>\n' \
                        '    <machine>%s</machine>\n' % \
                        (uname[1], uname[2], uname[4]))

        try:
            total_mem = os.sysconf("SC_PHYS_PAGES") * os.sysconf("SC_PAGE_SIZE")
            f_out.write('    <memory>%s</memory>\n' % total_mem)
        except:
            pass

        try:
            f_out.write('    <cpus>%u</cpus>\n' %
                        os.sysconf("SC_NPROCESSORS_ONLN"))
        except:
            pass

        f_out.write('  </sysinfo>\n')

        while True:
            buf = f_in.read(8192)
            if len(buf) == 0:
                break
            f_out.write(buf)

        f_out.write('  <metrics>\n')

        if start_info['duse'] != None and end_info['duse'] != None:
            f_out.write('    <diskusage start="%u" end="%u">%d</diskusage>\n' %
                        (start_info['duse'], end_info['duse'],
                         end_info['duse'] - start_info['duse']))

        f_out.write('    <!-- RUSAGE_CHILDREN -->\n' +
                    ''.join(map(lambda x: '    ' + x + '\n',
                                __to_xml("children", end_info['res_chld'],
                                         start_info['res_chld']))) +
                    '    <!-- RUSAGE_SELF -->\n' +
                    ''.join(map(lambda x: '    ' + x + '\n',
                                __to_xml("self", end_info['res_self'],
                                         start_info['res_self']))) +
                    "  </metrics>\n")
        f_out.write('</build>\n')
    finally:
        f_in.close()
        f_out.close()

def __write_task_complete(e, start_info, end_info):
    from bb.event import getName

    info = {
        'now' : end_info['time'],
        'PV'  : e.data.getVar('PV', True),
        'PR'  : e.data.getVar('PR', True),
        'PN'  : e.data.getVar('PN', True),
        'PF'  : e.data.getVar('PF', True),
        'pid' : start_info['pid'],
        'start_tm'     : start_info['time'],
        'start_tm_str' : time.strftime('%c', time.gmtime(start_info['time'])),
        'end_tm_str'   : time.strftime('%c', time.gmtime(end_info['time'])),
        'total_time' : end_info['time'] - start_info['time'],
        'result' : ['FAIL','OK'][getName(e) == "TaskSucceeded"],
        'preference' : e.data.getVar('DEFAULT_PREFERENCE', True) or '0',
        'task' : e.task }

    x = \
        '  <!-- %(task)s(%(PF)s) | %(start_tm_str)s - %(end_tm_str)s (PID %(pid)s) -->\n' \
        '  <task name="%(task)s" result="%(result)s" pn="%(PN)s" pv="%(PV)s" pr="%(PR)s" ' \
        'started="%(start_tm)s" ended="%(now)s" duration="%(total_time)f" preference="%(preference)s" pid="%(pid)s">\n' % info

    if end_info['duse'] != None:
        x = x + '    <info type="diskusage">%u</info>\n' % end_info['duse']

    __write(e.data, x +
            '    <!-- RUSAGE_CHILDREN -->\n' +
            ''.join(map(lambda x: '    ' + x + '\n',
                        __to_xml("children",
                                 end_info['res_chld'],
                                 start_info['res_chld']))) +
            '    <!-- RUSAGE_SELF -->\n' +
            ''.join(map(lambda x: '    ' + x + '\n',
                        __to_xml("self",
                                 end_info['res_self'],
                                 start_info['res_self']))) +

            '  </task>\n')

def __write_stamp(e, start_info, end_info, name):
    info = {
        'name' : name,
        'now' : end_info['time'],
        'pid' : start_info['pid'],
        'start_tm'     : start_info['time'],
        'start_tm_str' : time.strftime('%c', time.gmtime(start_info['time'])),
        'end_tm_str'   : time.strftime('%c', time.gmtime(end_info['time'])),
        'total_time' : end_info['time'] - start_info['time'],
        }

    x = '  <!-- stamp: %(name)s | %(start_tm_str)s - %(end_tm_str)s (PID %(pid)s) -->\n' \
        '  <stamp name="%(name)s" started="%(start_tm)s" ended="%(now)s" duration="%(total_time)f" ' \
        ' pid="%(pid)s">\n' % info

    if end_info['duse'] != None:
        x = x + '    <info type="diskusage">%u</info>\n' % end_info['duse']

    __write(e.data, x +
            '    <!-- RUSAGE_CHILDREN -->\n' +
            ''.join(map(lambda x: '    ' + x + '\n',
                        __to_xml("children", end_info['res_chld'],
                                 start_info['res_chld']))) +
            '    <!-- RUSAGE_SELF -->\n' +
            ''.join(map(lambda x: '    ' + x + '\n',
                        __to_xml("self", end_info['res_self'],
                                 start_info['res_self']))) +

            '  </stamp>\n')
    


def __get_duse(e):
    try:
        df = os.statvfs(e.data.getVar("TMPDIR", True))
        duse = df.f_bsize * (df.f_blocks - df.f_bavail)
    except OSError:
        duse = None

    return duse

def __record_resources(prop, e):
    import resource

    ev_data = (e.data.getVarFlag('_event_info', 'resources') or {})
    ev_data[prop] = { 'res_chld' : resource.getrusage(resource.RUSAGE_CHILDREN),
                      'res_self' : resource.getrusage(resource.RUSAGE_SELF),
                      'clock'    : time.clock(),
                      'time'     : time.time(),
                      'pid'      : os.getpid(),
                      'duse'     : __get_duse(e) }

    e.data.setVarFlag('_event_info', 'resources', ev_data)

def __handle_complete(e, prefix, name, fn):
    __record_resources(prefix + '_end', e)
    info = e.data.getVarFlags('_event_info')

    res_start = info['resources'][prefix + '_start']
    res_end   = info['resources'][prefix + '_end']

    if res_start['pid'] != res_end['pid']:
        bb.warn("PID mismatch in %s (%u vs %u)" %
                (name, res_start['pid'], res_end['pid']))

    fn(res_start, res_end)

def __write_ccache_stats(e, stage):
    import bb.process

    ccache = e.data.getVar("CCACHE", True) or "ccache"

    try:
        data = bb.process.run(ccache + " -s")[0]
    except Exception, exc:
        data = "    ... unavailable (%s)\n" % exc

    # todo: split it into machine readable xml
    __write(e.data,
            '  <ccache stage="%s"><![CDATA[\n' % stage +
            data +
            '  ]]></ccache>\n')

def eventhandler (e):
    from bb import note, error
    from bb.event import getName

    if e.data is None or getName(e) in ("MsgNote", "RecipePreFinalise", 
                                        "RecipeParsed", "ParseProgress", "StampUpdate"):
        return

    name = getName(e)
    #bb.note("event: %s" % name)

    # First event
    if name == "ParseStarted":
        tmp_fname = _metrics_tmpname(e.data)

        try:
            os.unlink(tmp_fname)
        except OSError:
            pass

        __write_ccache_stats(e, name)

    if name == "BuildStarted":
        __record_resources('build_start', e)
    elif name in "BuildCompleted":
        __write_ccache_stats(e, name)
        __handle_complete(e, 'build', 'BuildCompleted',
                          lambda res_start, res_end: 
                          __write_build_complete(e, res_start, res_end,
                                                 e.data.getVar('METRICS_FILE', True)))

    elif name == "TaskStarted":
        __record_resources('task_start', e)
    elif name in ("TaskSucceeded", "TaskFailed"):
        __handle_complete(e, 'task', 'task-complete',
                          lambda res_start, res_end:
                              __write_task_complete(e, res_start, res_end))
    elif name == 'ParseStarted':
        __record_resources('parse_start', e)
    elif name == 'ParseCompleted':
        __handle_complete(e, 'parse', 'ParseCompleted',
                          lambda res_start, res_end:
                              __write_stamp(e, res_start, res_end, 'parsing'))
    else:
    	#bb.warn("Unsupported event %s" % name)
        pass

    return
