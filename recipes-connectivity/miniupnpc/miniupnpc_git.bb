DESCRIPTION = "UPNP IGD port redirection"
SECTION = "connectivity"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c5ad90debd10be20aa95189a0a7c4253 "

SRC_URI[md5sum] = "0921e993f8061e07340251498cc838e5"
SRC_URI[sha256sum] = "8b6420a52074a1392660b8fc092bab5592b543b0a7694c84739ad965e16c0d58"

SRCREV = "2ae481b9693d56f0372df9bf8ca968d93302fc12"

SRC_URI = "git://github.com/karlhiramoto/miniupnp.git;branch=master;protocol=ssh;user=git"
S = "${WORKDIR}/git/miniupnpc"

#uncomment this to build code on local PC not on git repo
#inherit externalsrc
#S = "/home/karl/Work/miniupnp/miniupnpc"

EXTRA_OEMAKE = "\
    'CC=${CC}' \
    'CFLAGS=${CFLAGS} -DNEED_STRUCT_IP_MREQN ' \
    'LDFLAGS=${LDFLAGS}' \
"



do_compile () {
	cd ${S}
	oe_runmake
}


do_install () {
	install -d ${D}/usr
	install -d ${D}/usr/bin
#	install -d ${D}/usr/lib
	
	install -m 0755 ${S}/upnpc-static ${D}/usr/bin/upnpc
#	install -m 0755 ${S}/libminiupnpc.so ${D}/usr/lib
}

FILES_${PN} = "/usr/bin/upnpc"