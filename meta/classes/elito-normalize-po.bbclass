DEPENDS += "gettext"

PO_FILES = "`find -type f -name '*.po'`"

do_normalize_po() {
    for i in ${PO_FILES}; do
	echo "Converting $i"
    	msgconv -o $i- -t utf-8 $i
	mv $i- $i
    done
}
addtask do_normalize_po before do_compile after do_patch
