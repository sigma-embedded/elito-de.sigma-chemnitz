RM_OLD_PATTERN = "-[^-]\+-[^-]\+$"

do_rm_old_work() {
  b=`dirname "${WORKDIR}"`
  pn='${PN}'

  for d in "$b/$pn"*; do
      test -d "$d" || continue
      test x"$d" != x"${WORKDIR}" || continue

      v=${d##$b/$pn}
      expr "$v" : '${RM_OLD_PATTERN}' >/dev/null || continue

      for f in "$d"/* "$d"/.*; do
	  test -e "$f" || continue

	  case "$f" in
	    */.|*/..)
		  ;;

	    */temp)
		  ;;
	    *)
		  bbnote "Removing old $f"
		  rm -rf "$f"
		  ;;
	  esac
      done
  done
}

addtask rm_old_work before do_unpack
