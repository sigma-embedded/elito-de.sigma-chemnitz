do_rm_old_work() {
  b=`dirname "${WORKDIR}"`
  pn='${PN}'

  for d in "$b"/*; do
      test -d "$d" || continue
      test x"$d" != x"${WORKDIR}" || continue

      for f in "$d"/* "$d"/.*; do
	  test -e "$f" || continue
	  b=${f##$d/}

	  case $f in
	    */.|*/..)
		  ;;

	    */temp.tar.gz)
		  ;;

	    */temp)
		  bbnote "Packing old $f"
		  ( cd "$d" && tar czf "$b".tar.gz --remove-files "$b" )
		  ;;

	    *)
		  bbnote "Removing old $f"
		  rm -rf "$f"
		  ;;
	  esac
      done
  done
}

addtask rm_old_work before do_configure do_compile do_package
