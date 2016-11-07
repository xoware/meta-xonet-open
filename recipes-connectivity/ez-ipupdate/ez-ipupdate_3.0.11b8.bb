# Angus Mackay's ez-ipupdate from www.ez-ipupdate.com
DESCRIPTION = "A client for automatically updating your EZ-IP.net, justlinux.com, dhs.org, dyndns.org, ods.org, gnudip.cheapnet.net, tzo.com, easydns.com dynamic hostname parameters. Includes daemon support that only sends updates if your IP address changes."
HOMEPAGE = "http://www.ez-ipupdate.com/"
SECTION = "console/network"
#PRIORITY = "optional"
LICENSE = "GPL"
LIC_FILES_CHKSUM = "file://COPYING;md5=7783169b4be06b54e86730eb01bc3a31"
PR = "r4"

SRC_URI = "file://ez-ipupdate-preped.tar.gz" 



inherit autotools 

#uncomment this to build code on local PC not on git repo
# inherit externalsrc
#S = "/tmp/eiu/ez-ipupdate-3.0.11b8"


SRC_URI[sha256sum] = "d7a7399c622173ff8fa583c644253eff07fb74c2505f8d4b1616c1e355c070c1"
