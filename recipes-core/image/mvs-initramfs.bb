DESCRIPTION = "A small image just capable of allowing a device to boot."

IMAGE_LINGUAS = " "

LICENSE = "MIT"

inherit core-image

IMAGE_ROOTFS_SIZE = "8192"
BUSYBOX_SPLIT_SUID = "0"

# remove not needed ipkg information
#ROOTFS_POSTPROCESS_COMMAND += "remove_packaging_data_files ; "

LINUX_VERSION_EXTENSION = "-xoware"

PACKAGE_INSTALL = "busybox xoscripts-initramfs mtd-utils mtd-utils-ubifs"

LICENSE_FLAGS_WHITELIST += "commercial"
LICENSE_FLAGS_WHITELIST += "CLOSED "


KERNEL_IMAGETYPE = "uImage"
IMAGE_FSTYPES = "cpio.gz"
IMAGE_DEVICE_TABLES = "files/en_device_table.txt"
USE_DEVFS = "0"
