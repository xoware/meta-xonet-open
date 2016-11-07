#!/bin/bash

#    0x000060000000-0x00006004FFFF : "bootloader_bkup" : 64*5 KB
#    0x000060050000-0x00006005FFFF : "u-bootenv_bkup"  : 64*1 KB
#    0x000060060000-0x00006009FFFF : "bootloader"      : 64*4 KB
#    0x0000600A0000-0x0000600AFFFF : "u-bootenv"       : 64*1 KB
#    0x0000600B0000-0x0000604FFFFF : "kernel"          : 64*69KB
#    0x000060500000-0x0000607FFFFF : "empty/kernel2"   : 64*48KB

parts=(
"s1u-load	:5 "
"env		:1 "
"u-boot.bin	:4 "
"env		:1 "
"uImage-initramfs-xo1-mvs.bin	:89"
)

set -x

DEPLOY_DIR=$1
EK_VERSION=$2
cd $DEPLOY_DIR

OUTFILE=./exonet_nor_${EK_VERSION}.img
/bin/rm -f nor.img $OUTFILE

prt=1
for i in "${parts[@]}" ; do
	nam=$(echo $i | awk -F ':' '{print $1}')
	sect=$(echo $i | awk -F ':' '{print $2}')
	echo
	echo -------PART $prt : SIZE $(($sect* 0x10000)) : $nam-------

	if [ $nam != "env" ]; then
		[ -h $nam ] && nam=$(ls -l $nam | awk '{print $11}')
		if [ ! -f $nam ] ; then /bin/rm -f *.nor; exit 1 ; fi
		padz=$(( 0x10000 * $sect - $(stat $nam  | grep -i size | awk '{print $2}') ))
		cat $nam >> $OUTFILE
		dd if=/dev/zero  bs=1 count=$padz >> $OUTFILE
	else
		padz=$(( 0x10000 * $sect ))
		dd if=/dev/zero bs=1 count=$padz >> $OUTFILE
	fi
	prt=$((prt+1))
done;
ln -sf $OUTFILE nor.img
