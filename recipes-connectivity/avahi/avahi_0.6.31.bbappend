
FILESEXTRAPATHS_prepend := "${THISDIR}/files/:"

SRC_URI += "file://avahi-daemon.conf "
SRC_URI += "file://0001-Port-Sunil-Ghai-s-LLMNR-support-code-to-current-Avah.patch;apply=yes;striplevel=1 "

do_install_append() {
	rm -rf ${D}/etc/avahi/avahi-daemon.conf
	install -m 644 ${WORKDIR}/avahi-daemon.conf ${D}/etc/avahi/avahi-daemon.conf
}
