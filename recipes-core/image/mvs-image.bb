DESCRIPTION = "A small image just capable of allowing a device to boot."

IMAGE_LINGUAS = " "

LICENSE = "MIT"
DEPENDS = "gen-firmware-native mtools-native xomkimage-native"

inherit core-image deploy

EXTRA_IMAGEDEPENDS = "gen-firmware-native mtools-native xomkimage-native"

IMAGE_ROOTFS_SIZE = "8192"
BUSYBOX_SPLIT_SUID = "0"

# remove not needed ipkg information
#ROOTFS_POSTPROCESS_COMMAND += "remove_packaging_data_files ; "

LINUX_VERSION_EXTENSION = "-xoware"
#PREFERRED_VERSION_linux-yocto = "3.6.%"
#PREFERRED_VERSION_u-boot = "2008.10%"

MVS_PKGS = "xokd"
MVS_PKGS += "xoscripts"
MVS_PKGS += "mtd-utils"
MVS_PKGS += "mtd-utils-ubifs"
MVS_PKGS += "mtd-utils-jffs2"
MVS_PKGS += "mtd-utils-misc"
#MVS_PKGS += "openssl openssl-engines"
#MVS_PKGS += "openvpn"
MVS_PKGS += "iptables"
#MVS_PKGS += "cryptodev"
#MVS_PKGS += "af-alg-engine"
#MVS_PKGS += "cavium-nitrox"
MVS_PKGS += "e2fsprogs e2fsprogs-e2fsck e2fsprogs-mke2fs e2fsprogs-tune2fs e2fsprogs-badblocks"
MVS_PKGS += "miniupnpc"
MVS_PKGS += "zip"
MVS_PKGS += "openssl libssl libcrypto"
MVS_PKGS += "ez-ipupdate"
MVS_PKGS += "strongswan"
MVS_PKGS += "iproute2"
MVS_PKGS += "dnsmasq"
MVS_PKGS += "samba"
MVS_PKGS += "avahi-daemon"
MVS_PKGS += "libnfnetlink"
MVS_PKGS += "minissdpd"
MVS_PKGS += "miniupnpd"
MVS_PKGS += "glib-2.0"
MVS_PKGS += "libnice"
MVS_PKGS += "dropbear"
MVS_PKGS += "libpam"  
MVS_PKGS += "conntrack-tools"  



#Tools for now for debug/testing, remove for production
MVS_PKGS += "procps"
#MVS_PKGS += "gdb"
MVS_PKGS += "tcpdump"
MVS_PKGS += "strace"
MVS_PKGS += "socat"
MVS_PKGS += "iperf3"
MVS_PKGS += "ethtool"

MVS_PKGS += "libnl-route libnl-genl"
MVS_PKGS += "nmap"


#install all kernel modules
MVS_PKGS += "kernel-modules"

#EXTRA_IMAGEDEPENDS = "virtual/bootloader"
#uboot now provides an artifact for the image. No longer extra
MVS_PKGS += "u-boot-xo1"

IMAGE_INSTALL = "packagegroup-core-boot ${ROOTFS_PKGMANAGE_BOOTSTRAP} ${CORE_IMAGE_EXTRA_INSTALL}  ${MVS_PKGS}"


LICENSE_FLAGS_WHITELIST += "commercial"
RDEPENDS_kernel-base = ""

INITRAMFS_FSTYPES = "cpio.gz"
INITRAMFS_IMAGE = "mvs-initramfs"


gen_xoware_img() {
    XO_VERSION=`cat ${IMAGE_ROOTFS}/etc/XO_VERSION`
    echo "XO_VERSION = $XO_VERSION"
       
    gen_mvs_img.sh ${DEPLOY_DIR_IMAGE} ${XO_VERSION}

    #FIXME  uboot is not built
    #mknorimg.sh ${DEPLOY_DIR_IMAGE} ${XO_VERSION}
    
    #generate firmware image for update in linux UI
    xomkimage ${DEPLOY_DIR_IMAGE}/uImage-initramfs-xo1-mvs.bin:file:1:0:mmcblk0p1:uImage  ${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}.rootfs.squashfs:file:1:0:mmcblk0p1:rootfs.sqh  > ${DEPLOY_DIR_IMAGE}/XOnet_firmware_${XO_VERSION}.img
    ln -sf ${DEPLOY_DIR_IMAGE}/XOnet_firmware_${XO_VERSION}.img ${DEPLOY_DIR_IMAGE}/XOnet_firmware.img
}



IMAGE_POSTPROCESS_COMMAND = " gen_xoware_img ; "

FILESYSTEM_PERMS_TABLES = "files/fs-perms.txt"

