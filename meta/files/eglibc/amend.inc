# INC_PR="r22"

do_install_append_x86-64() {
	install -d ${D}${prefix}
	ln -sf lib ${D}/lib64
	ln -sf lib ${D}${prefix}/lib64
}

multiliblinks = ""
multiliblinks_x86-64 = "/lib64 ${prefix}/lib64"

libcfiles += "${multiliblinks}"
