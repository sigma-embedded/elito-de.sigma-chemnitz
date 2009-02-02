def ubi_get_nand_leb_size(d):
	import bb
	bsize = int(bb.data.getVar('NAND_BLOCKSIZE', d, 1),0)
	psize = int(bb.data.getVar('NAND_PAGESIZE',  d, 1),0)
	return bsize - 2*psize

def ubi_gen_ini_data(info_name,d):
	import bb
	import os.path

	deploy_dir = bb.data.getVar('DEPLOY_DIR', d, 1)
	space 	   = bb.data.getVar('FLASH_SIZE', d, 1)
	info 	   = bb.data.getVar(info_name, d, 1)

	if not info:
		return ""

	vols = [v.split() for v in info.split(',')]
	pos  = 0
	res  = []
	
	for (name, type, image, size) in vols:
		res.extend((
			'[%s-volume]' % name,
			'mode=ubi',
			'image=%s' % os.path.join(deploy_dir, 'images', image),
			'vol_id=%i' % pos,
			'vol_size=%i' % int(size,0),
			'vol_type=%s' % type,
			'vol_name=%s' % name, ''))
		pos = pos+1
	return '\n'.join(res)

def ubi_byte_to_leb(var_name, d):
	import bb
	src = int(bb.data.getVar(var_name, d), 0)
	return src/ubi_get_nand_leb_size(d)

ubi_gen_ini() {
	cat << EOF > ${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}.ubi.ini
${@ubi_gen_ini_data('UBI_VOLUMES', d)}
EOF
}

UBI_GEN_ENV      = :

DEPENDS_append_mobm320	 = " mobm320 mobm320-native"
UBI_GEN_ENV_mobm320	 =  ubi_gen_env_mobm320

ubi_gen_env_mobm320() {
	rm -f ${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}.rootfs.bootenv
	mobm320-create-env ${MOBM320_ENV_ARGS} > ${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}.rootfs.bootenv
}

NAND_PAGESIZE  	?= 2048
NAND_BLOCKSIZE 	?= 65536
FLASH_SIZE	?= 134217728

IMAGE_DEPENDS_append_ubifs = " mtd-utils-native"

mkfs.ubifs	?= "PATH=$PATH:/sbin:/usr/sbin; mkfs.ubifs"
ubinize		?= "PATH=$PATH:/sbin:/usr/sbin; ubinize"

NAND_LEB_SIZE	?= "${@ubi_get_nand_leb_size(d)}"
IMAGE_CMD_ubifs	 = "${mkfs.ubifs}	\
	--output ${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}.rootfs.ubifs		\
	--root ${IMAGE_ROOTFS}						\
	${EXTRA_IMAGECMD}"

IMAGE_CMD_ubinize = "ubi_gen_ini; ${UBI_GEN_ENV};			\
	${ubinize}							\
	--output ${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}.rootfs.ubinize	\
	${EXTRA_IMAGECMD}						\
	${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}.ubi.ini"

EXTRA_IMAGECMD_ubifs ?=	"	\
	--min-io-size ${NAND_PAGESIZE}	\
	--leb-size ${NAND_LEB_SIZE}	\
	--max-leb-cnt ${@ubi_byte_to_leb('ROOTFS_SIZE',d)}"

EXTRA_IMAGECMD_ubinize	?= "	\
	--peb-size ${NAND_BLOCKSIZE}	\
	--min-io-size ${NAND_PAGESIZE}"
