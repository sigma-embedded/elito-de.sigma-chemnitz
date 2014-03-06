IMAGE_CMD_arnoldboot = "\
  bootgen -d8 ${ROOTFS_OFFSET} ${ROOTFS_OFFSET} @${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}.rootfs.jffs2 > ${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}.rootfs.arnoldboot"

IMAGE_TYPEDEP_arnoldboot = "jffs2"
