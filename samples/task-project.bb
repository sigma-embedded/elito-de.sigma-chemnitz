DESCRIPTION  = "@PROJECT_NAME@ packagegroup"
LICENSE      = "GPLv3"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"

PV = "0.3"

inherit packagegroup
inherit elito-common

do_fetch[noexec] = "1"
do_unpack[noexec] = "1"
do_patch[noexec] = "1"
do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_install[noexec] = "1"
do_distribute_sources[noexec] = "1"
do_populate_sysroot[noexec] = "1"

# !! DO NOT MOVE IT TO TOP !!
# Else, the task class sets PACKAGE_ARCH to all which will override
# value here.
PACKAGE_ARCH = "${MACHINE_ARCH}"

# add recipes which are not catched by normal dep resolving and which
# would result into
#
# |  Collected errors:
# |    * ERROR: Cannot satisfy the following dependencies for elito-task-core:
#
# like errors during rootfs creation.
DEPENDS += "${ELITO_COMMON_DEPENDS}"
DEPENDS += "gdb-cross-${TARGET_ARCH}"

RDEPENDS_${PN} = "\
	files-@PROJECT_NAME@		\
"

RRECOMMENDS_${PN} = "\
	${ELITO_COMMON_KERNEL_MODULES}	\
	${ELITO_COMMON_DIAGNOSIS_TOOLS}	\
	${ELITO_COMMON_SERVERS}		\
	${ELITO_COMMON_PROGRAMS}	\
\
	helloworld 			\
"
