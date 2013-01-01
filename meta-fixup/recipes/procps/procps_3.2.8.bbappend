PRINC := "${@int('${PRINC}') + 1}"

do_install_append() {
	rm -f ${D}${sysconfdir}/sysctl.conf
}

python() {
    pn = d.getVar("PN", True)
    cf = d.getVar("CONFFILES_" + pn, False).split()

    try:
        cf.remove("${sysconfdir}/sysctl.conf")
    	d.setVar("CONFFILES_" + pn, ' '.join(cf))
    except:
        pass
}
