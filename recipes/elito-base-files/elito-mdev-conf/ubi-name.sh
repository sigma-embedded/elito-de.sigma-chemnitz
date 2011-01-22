#! /bin/sh

set -e

f=/sys/class/ubi/$MDEV/name
n=
! test -e "$f" || n=`cat "$f"` || n=

if test -n "$n"; then
    mkdir -p /dev/ubi && \
    ln -s "../$MDEV" "/dev/ubi/$n"
fi
