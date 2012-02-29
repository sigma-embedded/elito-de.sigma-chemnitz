def ubi_get_nand_leb_size(d):
	import bb
	bsize = int(d.getVar('NAND_BLOCKSIZE', True),0)
	psize = int(d.getVar('NAND_PAGESIZE',  True),0)
	return bsize - 2*psize

def ubi_gen_ini_data(info_name,d):
	import bb
	import os.path

	deploy_dir = d.getVar('DEPLOY_DIR', True)
	space	   = d.getVar('FLASH_SIZE', True)
	info	   = d.getVar(info_name, True)

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
	src = int(d.getVar(var_name, True), 0)
	return src/ubi_get_nand_leb_size(d)

ubi_gen_ini() {
	cat << EOF > ${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}.ubi.ini
${@ubi_gen_ini_data('UBI_VOLUMES', d)}
EOF
}

UBI_GEN_ENV = ":"
UBI_GEN_ENV_fs-mobm320 = "ubi_gen_env_mobm320"

ubi_gen_env_mobm320() {
	rm -f ${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}.rootfs.bootenv
	mobm320-create-env ${MOBM320_ENV_ARGS} > ${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}.rootfs.bootenv
}

NAND_PAGESIZE	?= "2048"
NAND_BLOCKSIZE	?= "65536"
FLASH_SIZE	?= "134217728"

NAND_LEB_SIZE	?= "${@ubi_get_nand_leb_size(d)}"

IMAGE_CMD_ubi    = "ubi_gen_ini; ${UBI_GEN_ENV};		\
	ubinize							\
	--output ${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}.rootfs.ubi	\
	${UBINIZE_ARGS}						\
	${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}.ubi.ini"

MKUBIFS_ARGS ?= "	\
	--min-io-size ${NAND_PAGESIZE}	\
	--leb-size ${NAND_LEB_SIZE}	\
	--max-leb-cnt ${@ubi_byte_to_leb('IMAGE_ROOTFS_SIZE',d)}"

UBINIZE_ARGS ?= "	\
	--peb-size ${NAND_BLOCKSIZE}	\
	--min-io-size ${NAND_PAGESIZE}"
