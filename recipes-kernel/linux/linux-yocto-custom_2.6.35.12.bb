# linux-yocto-custom.bb:
#
#   An example kernel recipe that uses the linux-yocto and oe-core
#   kernel classes to apply a subset of yocto kernel management to git
#   managed kernel repositories.
#
#   To use linux-yocto-custom in your layer, create a
#   linux-yocto-custom.bbappend file containing at least the following
#   lines:
#
#     FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
#     COMPATIBLE_MACHINE_yourmachine = "yourmachine"
#
#   You must also provide a Linux kernel configuration. The most direct
#   method is to copy your .config to files/defconfig in your layer,
#   in the same directory as the bbappend and add file://defconfig to
#   your SRC_URI.
#
#   To use the yocto kernel tooling to generate a BSP configuration
#   using modular configuration fragments, see the yocto-bsp and
#   yocto-kernel tools documentation.
#
# Warning:
#
#   Building this example without providing a defconfig or BSP
#   configuration will result in build or boot errors. This is not a
#   bug.
#
#
# Notes:
#
#   patches: patches can be merged into to the source git tree itself,
#            added via the SRC_URI, or controlled via a BSP
#            configuration.
#
#   example configuration addition:
#            SRC_URI += "file://smp.cfg"
#   example patch addition (for kernel v3.4 only):
#            SRC_URI += "file://0001-linux-version-tweak.patch
#   example feature addition (for kernel v3.4 only):
#            SRC_URI += "file://feature.scc"
#

inherit kernel
#require recipes-kernel/linux/linux-yocto.inc

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"
DEFAULT_PREFERENCE = "-1"


LINUX_VERSION = "2.6.35.12"
LINUX_VERSION_EXTENSION ?= "-custom"
KBRANCH = "master"
META = "meta"


# Override SRC_URI in a bbappend file to point at a different source
# tree if you do not want to build from Linus' tree.
#SRC_URI = "git://github.com/xoware/linux-2.6.35.12.git;branch=${KBRANCH};branch=master;protocol=ssh;user=git"

SRC_URI = "git://git.kernel.org/pub/scm/linux/kernel/git/torvalds/linux.git"

SRC_URI += "file://patch-2.6.35.12.patch"
SRC_URI += "file://econa.patch"
SRC_URI += "file://defconfig"
SRC_URI += "file://extra-cflags-override.patch"
SRC_URI += "file://no_upnas.patch"
SRC_URI += "file://paritions.patch"
SRC_URI += "file://xo1+5.patch"
SRC_URI += "file://xo1+5.2.patch"
SRC_URI += "file://fix-build-on-newer-perl-versions.patch"
SRC_URI += "file://add-compiler-gcc5.h.patch"
SRC_URI += "file://ftrace_return_address.patch"
SRC_URI += "file://no_force_cns3xxx.patch"


KERNEL_EXTRA_ARGS="PATCH_UPNAS=n LOADADDR=${UBOOT_LOADADDRESS}  V=1 KCFLAGS=-mno-unaligned-access"
#  EXTRA_CFLAGS=-mno-unaligned-access


# Override SRCREV to point to a different commit in a bbappend file to
# build a different release of the Linux kernel.
# tag: v3.4 76e10d158efb6d4516018846f60c2ab5501900bc
#3.10.11
#SRCREV="master"
SRCREV="v2.6.35"


#add recipie that has our initramfs
#INITRAMFS_IMAGE = "mvs-initramfs"



#PV = "${LINUX_VERSION}+${SRCREV}"

PV = "${LINUX_VERSION}"

PR = "r2"

S = "${WORKDIR}/git"

# Override COMPATIBLE_MACHINE to include your machine in a bbappend
# file. Leaving it empty here ensures an early explicit build failure.
COMPATIBLE_MACHINE = "(xo1-mvs)"

#KERNEL_CC = "${TARGET_PREFIX}gcc  -mno-thumb-interwork -marm -mno-unaligned-access"

#HACK. Gcc 5.2.0 is known to build but then crashes at run time (karl  march 2016)
#workaround is to specify path to an armv6 toolchain  gcc 4.7.2

KERNEL_CC = "/opt/poky/1.4.4/sysroots/x86_64-pokysdk-linux/usr/bin/armv6-vfp-poky-linux-gnueabi/arm-poky-linux-gnueabi-gcc  -mno-thumb-interwork -marm -mno-unaligned-access"
KERNEL_LD = "/opt/poky/1.4.4/sysroots/x86_64-pokysdk-linux/usr/bin/armv6-vfp-poky-linux-gnueabi/arm-poky-linux-gnueabi-ld"

KERNEL_CONFIG_COMMAND ?= "oe_runmake_call -C ${S} O=${B} oldnoconfig || yes '' | oe_runmake -C ${S} O=${B} oldconfig"


kernel_do_configure() {
	# fixes extra + in /lib/modules/2.6.37+
	# $ scripts/setlocalversion . => +
	# $ make kernelversion => 2.6.37
	# $ make kernelrelease => 2.6.37+
	touch ${B}/.scmversion ${S}/.scmversion

	cp ${WORKDIR}/defconfig ${B}/.config

	if [ ! -z "${INITRAMFS_IMAGE}" ]; then
		for img in cpio.gz cpio.lzo cpio.lzma cpio.xz; do
		if [ -e "${DEPLOY_DIR_IMAGE}/${INITRAMFS_IMAGE}-${MACHINE}.$img" ]; then
			cp "${DEPLOY_DIR_IMAGE}/${INITRAMFS_IMAGE}-${MACHINE}.$img" initramfs.$img
		fi
		done
	fi
	eval ${KERNEL_CONFIG_COMMAND}

}

