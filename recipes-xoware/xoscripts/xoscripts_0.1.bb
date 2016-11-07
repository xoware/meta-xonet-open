DESCRIPTION = "Custom setup files"
LICENSE = "CLOSED"
PR = "r1"
DEPENDS = "openssl"

SRC_URI =  "file://profile \
            file://.keep \
            file://firstboot \
            file://storage \
            file://rcS \
            file://rcK \
            file://inittab \
            file://nitrox \
            file://S80xokd \
            file://xokd-watcher.sh \
            file://coredumps \
	    file://vcrt.pem \
	    file://venc.pem \
	    file://ca-certificates.crt \
            file://vpex-ca-certs.pem  \
            file://check_ssl.sh"

PACKAGES = "${PN}"

FILES_${PN} = "/sbin/* ${sysconfdir} /xokcfg /usr /storage"

do_install () {
	install -d ${D}/sbin
	install -d ${D}/etc
	install -d ${D}/etc/openvpn
	install -d ${D}/etc/ssl
	install -d ${D}/etc/ssl/certs
	install -d ${D}/etc/init.d
	install -d ${D}/etc/profile.d
	install -d ${D}/xokcfg
	install -d ${D}/storage
	install -d ${D}/usr
	install -d ${D}/usr/bin
	install -d ${D}/usr/lib
	install -d ${D}/usr/lib/ssl
	install -m 0755 ${WORKDIR}/xokd-watcher.sh  ${D}/usr/bin
	install -m 0444 ${WORKDIR}/ca-certificates.crt ${D}/etc/ssl/certs/ca-certificates.crt
	install -m 0444 ${WORKDIR}/vpex-ca-certs.pem ${D}/etc/ssl/certs/vpex-ca-certs.pem
	install -m 0444 ${WORKDIR}/venc.pem ${D}/etc/ssl/certs/venc.pem
	install -m 0444 ${WORKDIR}/vcrt.pem ${D}/etc/ssl/certs/vcrt.pem
	install -m 0755 ${WORKDIR}/profile ${D}/etc/profile.d/
	install -m 0755 ${WORKDIR}/check_ssl.sh ${D}/sbin/check_ssl.sh
	install -m 0444 ${WORKDIR}/.keep ${D}/xokcfg/
	install -m 0444 ${WORKDIR}/.keep ${D}/storage/
	install -m 0755 ${WORKDIR}/firstboot ${D}/etc/init.d/S10firstboot
	install -m 0755 ${WORKDIR}/storage ${D}/etc/init.d/S20storage
	install -m 0755 ${WORKDIR}/coredumps ${D}/etc/init.d/S25coredumps
	install -m 0755 ${WORKDIR}/nitrox ${D}${sysconfdir}/init.d/S30nitrox
	install -m 0755 ${WORKDIR}/rcS ${D}${sysconfdir}/init.d/rcS
	install -m 0755 ${WORKDIR}/rcK ${D}${sysconfdir}/init.d/rcK
	install -m 0755 ${WORKDIR}/S80xokd ${D}${sysconfdir}/init.d/
	install -m 0644 ${WORKDIR}/inittab ${D}${sysconfdir}/
	ln -sf  syslog.busybox ${D}/etc/init.d/S00syslog
	ln -sf  populate-volatile.sh ${D}/etc/init.d/S37populate-volatile.sh
	ln -sf  /tmp/resolv.conf ${D}/etc/resolv.conf
	echo 3.1.`date +%Y%m%d%H%M` > ${D}/etc/XO_VERSION

}
