require u-boot.inc

# To build u-boot for your machine, provide the following lines in your machine
# config, replacing the assignments as appropriate for your machine.
# UBOOT_MACHINE = "omap3_beagle_config"
# UBOOT_ENTRYPOINT = "0x80008000"
# UBOOT_LOADADDRESS = "0x80008000"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=4c6cde5df68eff615d36789dc18edd3b \
                    file://README;beginline=1;endline=22;md5=ad8b2e82fdb62c692f9eb646748d8fe5"

# This revision corresponds to the tag "v2011.03"
# We use the revision in order to avoid having to fetch it from the repo during parse
SRCREV = "b59b16ca24bc7e77ec113021a6d77b9b32fcf192"

PV = "v2008.10+git${SRCPV}"
PR = "r8"

SRC_URI = "\
	git://git.denx.de/u-boot.git;protocol=git \
	file://uboot.patch;apply=yes;striplevel=1 \
	file://uboot1+5.patch;apply=yes;striplevel=1 \
	file://uboot1+5.2.patch;apply=yes;striplevel=1 \
	file://uboot1+5.3.patch;apply=yes;striplevel=1 \
	file://uboot_xo1.patch;apply=no;striplevel=1 \
	file://fw_env.config \
	"

S = "${WORKDIR}/git"

PACKAGE_ARCH = "${MACHINE_ARCH}"
EXTRA_OEMAKE = " CROSS_COMPILE=${TARGET_PREFIX} 'HOSTCC=${BUILD_CC}' 'HOSTSTRIP=${BUILD_STRIP}'"

SPL_BINARY = "s1u-load"

DEPENDS = "zlib"
FILES_${PN} = "${bindir}/fw_* ${sysconfdir}/fw_*"

do_compile () {
	if [ "${@base_contains('DISTRO_FEATURES', 'ld-is-gold', 'ld-is-gold', '', d)}" = "ld-is-gold" ] ; then
		sed -i 's/$(CROSS_COMPILE)ld$/$(CROSS_COMPILE)ld.bfd/g' config.mk
	fi

	unset LDFLAGS
	unset CFLAGS
	unset CPPFLAGS
	oe_runmake ${UBOOT_MACHINE}
	oe_runmake ${UBOOT_MAKE_TARGET}
	mv -f u-boot ${SPL_BINARY}
	mv -f u-boot.srec ${SPL_BINARY}.srec
	mv -f u-boot.bin ${SPL_BINARY}
#	patch --posix -l -p1 < ../uboot_xo1.patch
#	patch --posix -l -p1 < ../0002-uboot_xo1.patch
	oe_runmake clean
	oe_runmake ${UBOOT_MACHINE}
	oe_runmake ${UBOOT_MAKE_TARGET}

	${CC}  -Wall -DUSE_HOSTCC tools/env/fw_env.c tools/env/fw_env_main.c -o tools/env/fw_env -lz
}

do_recompile () {
	oe_runmake ${UBOOT_MACHINE}
	oe_runmake ${UBOOT_MAKE_TARGET}
}

addtask recompile

do_install_append () {
	install -d ${D}/${bindir}
	install -m 755 ${S}/tools/env/fw_env ${D}/${bindir}/fw_env
	ln -sf ${bindir}/fw_env ${D}/${bindir}/fw_printenv
	ln -sf ${bindir}/fw_env ${D}/${bindir}/fw_setenv
}
