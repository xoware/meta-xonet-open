DESCRIPTION = "UPNP SSDP port multiplexer"
SECTION = "connectivity"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ea6862bcd71f55f20a60e6580b26064d"

SRC_URI = "file://miniupnpd-1.8.tar.gz \
		file://Makefile \
		file://config.h \
		file://commonrdr.h \
		file://miniupnpd.c \
		file://upnpsoap.c \
		file://miniupnpd.conf \
	"
#S = "${WORKDIR}/git/miniupnpd"

#uncomment this to build code on local PC not on git repo
# inherit externalsrc
#S = "/tmp/ssdp/miniupnpd"

DEPENDS = "libnfnetlink"
RDEPENDS_${PN} = "minissdpd"
INSANE_SKIP_${PN} += "already-stripped"

do_compile () {
	cp -f ${WORKDIR}/Makefile ${S}/Makefile
	cp -f ${WORKDIR}/config.h ${S}/config.h
	cp -f ${WORKDIR}/commonrdr.h ${S}/commonrdr.h
	cp -f ${WORKDIR}/miniupnpd.c ${S}/miniupnpd.c
	cp -f ${WORKDIR}/upnpsoap.c ${S}/upnpsoap.c
	cp -f ${WORKDIR}/miniupnpd.conf ${S}/miniupnpd.conf
	oe_runmake
}

do_install () {
        oe_runmake install PREFIX=${D}
}



#SRC_URI[sha256sum] = "d4ac0fac6798e3ccf1972fef96e8913a8fb28e772c12b1549ea02c9388a60b85"
