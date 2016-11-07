DESCRIPTION = "Firmware Image generation"
SECTION = "bsp"
LICENSE = "CLOSED"


inherit externalsrc native
SRC_URI = "file://gen_mvs_img.sh "
SRC_URI += "file://mknorimg.sh "

S = "${THISDIR}/src"


do_install_append () {
	echo THISDIR = ${THISDIR}
	
	install -m 755 ${THISDIR}/files/gen_mvs_img.sh ${STAGING_BINDIR_NATIVE}
	install -m 755 ${THISDIR}/files/mknorimg.sh ${STAGING_BINDIR_NATIVE}
	
}
