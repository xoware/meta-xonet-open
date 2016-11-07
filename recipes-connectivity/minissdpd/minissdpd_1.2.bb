DESCRIPTION = "UPNP SSDP port multiplexer"
SECTION = "connectivity"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2e57ba78bb888a4762cf4195fccf6e48"

SRC_URI = "file://minissdpd-1.2.tar.gz"
#S = "${WORKDIR}/git/minissdpd"

#uncomment this to build code on local PC not on git repo
# inherit externalsrc
#S = "/tmp/ssdp/minissdpd"


do_compile () {
	oe_runmake
}

do_install () {
	oe_runmake install PREFIX=${D}
}



#SRC_URI[sha256sum] = "d4ac0fac6798e3ccf1972fef96e8913a8fb28e772c12b1549ea02c9388a60b85"
