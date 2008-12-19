#! /bin/sh

set -e

CACHE_DIR=$1
TARBALL=$2
co_dir=sources/svn-pre/

: ${SVN:=svn}
: ${BITBAKE_REV:=1118}
: ${BITBAKE_SVN_URI:=http://svn.berlios.de/svnroot/repos/bitbake/trunk/bitbake}

: ${TAR:=tar}
: ${TAR_FLAGS:=--owner root --group root --mode go-w,a+rX,u+w}

: ${TARBALL:=bitbake-${BITBAKE_REV}.tar.bz2}

cd "$CACHE_DIR"

test ! -s "$TARBALL" || exit 0

mkdir -p "$co_dir"
rm -rf "$co_dir"/bitbake

${SVN} export -r ${BITBAKE_REV} ${BITBAKE_SVN_URI}@${BITBAKE_REV} "$co_dir"/bitbake

rm -f "$TARBALL".tmp "$TARBALL"
( cd "$co_dir" && ${TAR} cjf - bitbake ${TAR_FLAGS} ) > "$TARBALL".tmp
mv "$TARBALL".tmp "$TARBALL"
