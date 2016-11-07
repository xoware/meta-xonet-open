#!/bin/bash
#
# This script writes the firmware binary to the flash MTD device

#
DEPLOY_DIR=$1
EK_VERSION=$2
cd $DEPLOY_DIR

set -x

FIRMWARE_BIN=./ExoNet_DOS_Image_${EK_VERSION}.sh
#ROOT_FS_IMG="exokey-image-exokey.squashfs"
ROOT_FS_IMG="mvs-image-xo1-mvs.squashfs"
KERNEL_IMG="uImage-initramfs-xo1-mvs.bin"
PART_IMG="disk.img"

rm -f ${PART_IMG}  ${FIRMWARE_BIN} rootfs.sqh

# cp -f ${ROOT_FS_IMG} rootfs.sqh

dd if=/dev/zero of=${PART_IMG} count=62k bs=1k 
# mkfs.msdos ${PART_IMG} 
# pwd
# mdir -i ${PART_IMG}
# echo "copy kernel ${KERNEL_IMG} and rootfs"
# mcopy -i ${PART_IMG} rootfs.sqh  ${KERNEL_IMG} ::


cat << 'EOS' > ${FIRMWARE_BIN}
#!/bin/sh

echo "### Starting FW Update"

echo $BASH_SOURCE

if [ $(id -u ) -ne 0 ]; then 
        echo " Please run this script as root"
fi

die() {
  echo "ERROR: $@" 1>&2
#  umount /mnt/tmp >& /dev/null
  exit 1
}

usage() {
  echo "Usage: $0 <sd device> "  1>&2
  exit 1
}

[ $# -eq 1 ] || usage

: ${SKIP_VRFY:="false"}
: ${SKIP_PREQ:="false"}
: ${SKIP_VOLS:="false"}
: ${SKIP_DATA:="false"}
if [ "$ONLY_VRFY" == "true" ]; then
	SKIP_PREQ="true"
	SKIP_VOLS="true"
	SKIP_DATA="true"
fi
if [ "$ONLY_VOLS" == "true" ]; then
	SKIP_VRFY="true"
	SKIP_PREQ="true"
	SKIP_DATA="true"
fi
if [ "$ONLY_DATA" == "true" ]; then
	SKIP_VRFY="true"
	SKIP_PREQ="true"
	SKIP_VOLS="true"
fi


SD_DEV=$1
echo SD_DEV=${SD_DEV}

fwchksum=""
do_chksum() {
	
	myself=$BASH_SOURCE
	
	cfwchksum=$(cat $myself | sed 's/^fwchksum=.*/fwchksum=""/' | md5sum | awk '{print $1}')
	
	if [ $cfwchksum != $fwchksum ]; then
		echo "$fwchksum"
		echo "$cfwchksum"
	        die "CheckSum mismatch, please reobtain this Package"
	fi
}

do_prereq() {
	echo "### checking prerequisites"
	! mount | grep $SD_DEV || die "SD appears to be mounted"
	! grep -w $SD_DEV /etc/fstab || die "SD device is in fstab, probably not the device you intended"
	[ -b $SD_DEV ] || die " SD device is not good"
}


do_volumes() {
	#wipe out disk to init partitions 
	dd if=/dev/zero of=${SD_DEV} count=1 bs=256k
	
	#setup partitions (rootfs + kernel), ( backup/upgrad rootfs+kernel), (config),  (storage)
	(echo "n"; echo "p"; echo "1"; echo ""; echo "+128M"; echo "t"; echo "c"; \
	echo ""; echo "n"; echo "p"; echo "2"; echo ""; echo "+128M"; echo "t"; echo "2"; echo "c"; \
	echo ""; echo "n"; echo "p"; echo "3"; echo ""; echo "+128M"; echo "t"; echo "3"; echo "83"; \
	echo "n"; echo "p"; echo "4"; echo ""; echo ""; echo "t"; echo "4"; echo "83";\
	echo ""; echo "p"; echo "w") |fdisk ${SD_DEV} 
	
	partprobe $SD_DEV
	hdparm -z $SD_DEV
	
	sleep 5;
	
	eval SD_ROOT1=${SD_DEV}*1
	eval SD_ROOT2=${SD_DEV}*2
	eval SD_CONFIG=${SD_DEV}*3
	eval SD_STORAGE=${SD_DEV}*4
	
	
	MMC_DEV=${SD_ROOT1}
	count=10
	while [ ${count} -gt 0 ]
	do 
		count=$((count-1))
	    
		if [ -b ${MMC_DEV} ]
		then
			echo "block device ${MMC_DEV} detected."
			break
		fi
		sleep 1
	done
	
	if [ ! -b ${MMC_DEV} ]
	then
		echo "Timeout detecting ${MMC_DEV}"
		exit1
	fi;
	
	#dd if=${PART_IMG} of=${SD_ROOT1} bs=64k
	#dd if=${PART_IMG} of=${SD_ROOT1} bs=64k
	
	mkfs.vfat ${SD_ROOT1}
	mkdir -p /mnt/tmp
	mount ${SD_ROOT1} /mnt/tmp
}


write_kernel() {
echo "### writing image to ${SD_DEV}"
base64 -d << 'LZT_EFS' | cat > /mnt/tmp/uImage
EOS

#create this with the following command
base64 ${KERNEL_IMG} >> ${FIRMWARE_BIN}
cat << 'EOS' >>  ${FIRMWARE_BIN}
LZT_EFS
echo "kernel Status =$?"
}

write_rootfs() {
echo "### writing image to ${SD_DEV}"
base64 -d << 'LZT_EFS' | cat > /mnt/tmp/rootfs.sqh
EOS

#create this with the following command
base64 ${ROOT_FS_IMG} >> ${FIRMWARE_BIN}
cat << 'EOS' >>  ${FIRMWARE_BIN}
LZT_EFS
echo "root Status =$?"
}

if [ "$SKIP_VRFY" != "true" ]; then
	do_chksum
fi
if [ "$SKIP_PREQ" != "true" ]; then
	do_prereq
fi
if [ "$SKIP_VOLS" != "true" ]; then
	do_volumes
fi
if [ "$SKIP_DATA" != "true" ]; then
	write_kernel
	write_rootfs
	umount /mnt/tmp
fi

EOS

chmod 755 ${FIRMWARE_BIN}
cfwchksum=$(cat ${FIRMWARE_BIN} | md5sum | awk '{print $1}')
sed -i "s/^fwchksum=.*/fwchksum=\"$cfwchksum\"/" ${FIRMWARE_BIN}
rm -f ExoNet_DOS_Image.sh
ln -s ${FIRMWARE_BIN} ExoNet_DOS_Image.sh
