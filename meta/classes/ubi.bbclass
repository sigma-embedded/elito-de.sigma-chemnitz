# --*- python -*--
def ubi_get_nand_leb_size(d):
    import bb
    bsize = int(d.getVar('NAND_BLOCKSIZE', True),0)
    psize = int(d.getVar('NAND_PAGESIZE',  True),0)
    return bsize - 2*psize

def ubi_gen_ini_data(info_name,d):
    import bb
    import os.path

    psize      = int(d.getVar('NAND_PAGESIZE',  True),0)
    deploy_dir = d.getVar('DEPLOY_DIR', True)
    space      = d.getVar('FLASH_SIZE', True)
    info       = d.getVar(info_name, False)

    if not info:
        return ""

    vols = [map(lambda x: d.expand(x), v.split()) for v in info.split(',')]
    pos  = 0
    res  = []

    for (name, type, image, size) in vols:
        size = int(size, 0)
        size = (size + psize - 1) / psize * psize

        res.extend((
            '[%s-volume]' % name,
            'mode=ubi',
            '%simage=%s' %
            (['','#'][image == '/dev/null'],
             os.path.join(deploy_dir, 'images', image)),
            'vol_id=%i' % pos,
            'vol_size=%u' % size,
            'vol_type=%s' % type,
            'vol_name=%s' % name, ''))
        pos = pos+1
    return '\n'.join(res)

def ubi_byte_to_leb(var_name, d):
    import bb
    src = int(d.getVar(var_name, True), 0) * 1024
    return int(src/ubi_get_nand_leb_size(d))

def ubi_add_ubifs(sizes, d):
    types = []
    sz = sizes.split()

    d.setVar('NAND_BLOCKSIZE', None, parsing=True)
    d.setVar('NAND_PAGESIZE', None, parsing=True)
    d.setVar('FLASH_SIZE', None, parsing=True)

    for t in d.getVar('IMAGE_FSTYPES', True).split():
        if t != 'ubifs':
            types.append(t)
        else:
            types.extend(map(lambda x: '%s-%s' % (t, x), sz))

    d.setVar('IMAGE_FSTYPES', ' '.join(types))

    overrides = d.getVar('OVERRIDES', True)
    deps = []
    for s in sz:
        ld = d.createCopy()
        ld.setVar('OVERRIDES', '%s:nand-%s' % (overrides, s))
        bb.data.update_data(ld)

        # Delete DATETIME so we don't expand any references to it now
        # This means the task's hash can be stable rather than having hardcoded
        # date/time values. It will get expanded at execution time.
        # Similarly TMPDIR since otherwise we see QA stamp comparision problems
        #
        # NOTE: this works because variables will stay as ${DATETIME} in the
        #       expanded cmd
        ld.delVar('DATETIME')
        ld.delVar('TMPDIR')

        #ld.finalize(False)

        cmds = []
        t    = 'ubifs-%s' % s
        task = 'do_image_%s' % t.replace('-', '_')
        img_cmd = ld.getVar('IMAGE_CMD_ubifs', True)

        if img_cmd:
            img_cmd = img_cmd.replace('rootfs.ubifs', 'rootfs.%s' % t)
            cmds.append('\t' + img_cmd)

        cmds.append("\tcd ${IMGDEPLOYDIR}")

        d.setVar(task, '\n'.join(cmds))
        d.setVarFlag(task, 'func', '1')
        d.setVarFlag(task, 'fakeroot', '1')
        d.setVarFlag(task, 'prefuncs', 'set_image_size')
        d.setVarFlag(task, 'postfuncs', 'create_symlinks')
        d.setVarFlag(task, 'subimages', t)
        d.appendVarFlag(task, 'vardeps',
                        ld.getVarFlag('IMAGE_CMD_ubifs', 'vardeps', True) or "")
        d.appendVarFlag(task, 'vardepsexclude', 'DATETIME')

        bb.build.addtask(task, 'do_image_complete', 'do_image', d)

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
