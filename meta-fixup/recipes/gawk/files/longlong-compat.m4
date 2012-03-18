# This macro is obsolescent and should go away soon.
AC_DEFUN([gl_AC_TYPE_LONG_LONG],
[
  AC_REQUIRE([AC_TYPE_LONG_LONG_INT])
  ac_cv_type_long_long=$ac_cv_type_long_long_int
  if test $ac_cv_type_long_long = yes; then
    AC_DEFINE(HAVE_LONG_LONG, 1,
      [Define if you have the 'long long' type.])
  fi
])
