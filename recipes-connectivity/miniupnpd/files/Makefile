# $Id: Makefile.linux,v 1.75 2012/08/24 18:09:20 nanard Exp $
# MiniUPnP project
# (c) 2006-2012 Thomas Bernard
# http://miniupnp.free.fr/ or http://miniupnp.tuxfamily.org/
# Author : Thomas Bernard
# for use with GNU Make
#
# options can be passed to genconfig.sh through CONFIG_OPTIONS :
# $ CONFIG_OPTIONS="--ipv6 --igd2" make -f Makefile.linux
#
# To install use :
# $ PREFIX=/dummyinstalldir make -f Makefile.linux install
# or :
# $ INSTALLPREFIX=/usr/local make -f Makefile.linux install
# or :
# $ make -f Makefile.linux install
#
# if your system hasn't iptables libiptc headers and binary correctly
# installed, you need to get iptables sources from http://netfilter.org/
# ./configure them and build them then miniupnpd will build using :
# $ IPTABLESPATH=/path/to/iptables-1.4.1 make -f Makefile.linux
#
#CFLAGS = -O -g -DDEBUG
CFLAGS ?= -Os
CFLAGS += -fno-strict-aliasing
CFLAGS += -fno-common
CFLAGS += -D_GNU_SOURCE
CFLAGS += -Wall
CFLAGS += -Wextra -Wstrict-prototypes -Wdeclaration-after-statement
#CFLAGS += -Wno-missing-field-initializers
#CFLAGS += -ansi	# iptables headers does use typeof which is a gcc extension
CC ?= gcc
RM = rm -f
INSTALL = install
STRIP ?= strip
CP = cp


INSTALLPREFIX ?= $(PREFIX)/usr
SBININSTALLDIR = $(INSTALLPREFIX)/sbin
ETCINSTALLDIR = $(PREFIX)/etc/miniupnpd
MANINSTALLDIR = $(INSTALLPREFIX)/share/man/man8

BASEOBJS = miniupnpd.o upnphttp.o upnpdescgen.o upnpsoap.o \
           upnpreplyparse.o minixml.o \
           getifaddr.o daemonize.o upnpglobalvars.o \
           options.o upnppermissions.o minissdp.o \
           upnpevents.o upnputils.o getconnstatus.o \

#           upnpredirect.o upnppinhole.o

LNXOBJS = linux/getifstats.o linux/ifacewatcher.o linux/getroute.o
NETFILTEROBJS = netfilter/iptcrdr.o netfilter/iptpinhole.o

ALLOBJS = $(BASEOBJS) $(LNXOBJS)

ifeq "$(wildcard /etc/gentoo-release )" ""
LIBS ?= 
else # gentoo
# the following is better, at least on gentoo with iptables 1.4.6
# see http://miniupnp.tuxfamily.org/forum/viewtopic.php?p=1618
# and http://miniupnp.tuxfamily.org/forum/viewtopic.php?p=2183
LIBS ?= -lip4tc
CFLAGS := -DIPTABLES_143 $(CFLAGS)
endif

ARCH ?= $(shell uname -m | grep -q "x86_64" && echo 64)
ifdef IPTABLESPATH
CFLAGS := $(CFLAGS) -I$(IPTABLESPATH)/include/
LDFLAGS := $(LDFLAFGS) -L$(IPTABLESPATH)/libiptc/
# get iptables version and set IPTABLES_143 macro if needed
ifeq ($(TARGET_OPENWRT),)
IPTABLESVERSION := $(shell grep "\#define VERSION" $(IPTABLESPATH)/config.h | tr -d \" |cut -d" " -f3 )
IPTABLESVERSION1 := $(shell echo $(IPTABLESVERSION) | cut -d. -f1 )
IPTABLESVERSION2 := $(shell echo $(IPTABLESVERSION) | cut -d. -f2 )
IPTABLESVERSION3 := $(shell echo $(IPTABLESVERSION) | cut -d. -f3 )
# test if iptables version >= 1.4.3
TEST := $(shell [ \( \( $(IPTABLESVERSION1) -ge 1 \) -a \( $(IPTABLESVERSION2) -ge 4 \) \) -a \( $(IPTABLESVERSION3) -ge 3 \) ] && echo 1 )
ifeq ($(TEST), 1)
CFLAGS := $(CFLAGS) -DIPTABLES_143
# the following sucks, but works
LIBS = $(IPTABLESPATH)/libiptc/.libs/libip4tc.o
#LIBS = $(IPTABLESPATH)/libiptc/.libs/libiptc.a
else # ifeq ($(TEST), 1)
LIBS = $(IPTABLESPATH)/libiptc/libiptc.a
endif # ifeq ($(TEST), 1)
else # ($(TARGET_OPENWRT),)
# openWRT :
# check for system-wide iptables files. Test if iptables version >= 1.4.3
# the following test has to be verified :
TEST := $(shell test -f /usr/include/iptables/internal.h && grep -q "\#define IPTABLES_VERSION" /usr/include/iptables/internal.h && echo 1)
ifeq ($(TEST), 1)
CFLAGS := $(CFLAGS) -DIPTABLES_143
LIBS = -liptc
endif  # ($(TEST), 1)
TEST_LIB := $(shell test -f /usr/lib$(ARCH)/libiptc.a && echo 1)
ifeq ($(TEST_LIB), 1)
LIBS = -liptc /usr/lib$(ARCH)/libiptc.a
endif # ($(TEST_LIB), 1)
endif # ($(TARGET_OPENWRT),)
else # ifdef IPTABLESPATH
# IPTABLESPATH not defined
# the following test has to be verified :
TEST := $(shell test -f /usr/include/xtables.h && grep -q "XTABLES_VERSION_CODE" /usr/include/xtables.h && echo 1)
ifeq ($(TEST), 1)
CFLAGS := $(CFLAGS) -DIPTABLES_143
LIBS = -liptc
TESTIP4TC := $(shell test -f /lib/libip4tc.so && echo 1)
ifeq ($(TESTIP4TC), 1)
LIBS := $(LIBS) -lip4tc
endif # ($(TESTIP4TC), 1)
TESTIP6TC := $(shell test -f /lib/libip6tc.so && echo 1)
ifeq ($(TESTIP6TC), 1)
LIBS := $(LIBS) -lip6tc
endif # ($(TESTIP6TC), 1)
endif # ($(TEST), 1)
endif # ifdef IPTABLESPATH

LIBS += -lnfnetlink
LDLIBS += $(LIBS)

TESTUPNPDESCGENOBJS = testupnpdescgen.o upnpdescgen.o

EXECUTABLES = miniupnpd testupnpdescgen testgetifstats \
              testupnppermissions miniupnpdctl testgetifaddr \
              testgetroute

.PHONY:	all clean install depend genuuid

all:	$(EXECUTABLES)

clean:
	$(RM) $(ALLOBJS)
	$(RM) $(EXECUTABLES)
	$(RM) testupnpdescgen.o testgetifstats.o
	$(RM) testupnppermissions.o testgetifaddr.o
	$(RM) testgetroute.o
	$(RM) miniupnpdctl.o

install:	miniupnpd miniupnpd.8 miniupnpd.conf genuuid \
	linux/miniupnpd.init.d.script
	$(STRIP) miniupnpd
	$(INSTALL) -d $(SBININSTALLDIR)
	$(INSTALL) miniupnpd $(SBININSTALLDIR)
	$(INSTALL) -d $(ETCINSTALLDIR)
	$(INSTALL) --mode=0644 -b miniupnpd.conf $(ETCINSTALLDIR)
	$(INSTALL) -d $(PREFIX)/etc/init.d
	$(INSTALL) linux/miniupnpd.init.d.script $(PREFIX)/etc/init.d/miniupnpd
	$(INSTALL) -d $(MANINSTALLDIR)
	$(INSTALL) miniupnpd.8 $(MANINSTALLDIR)/miniupnpd.8
	gzip $(MANINSTALLDIR)/miniupnpd.8

# genuuid is using the uuidgen CLI tool which is part of libuuid
# from the e2fsprogs
genuuid:
ifeq ($(TARGET_OPENWRT),)
	sed -i -e "s/^uuid=[-0-9a-f]*/uuid=`(genuuid||uuidgen||uuid) 2>/dev/null`/" miniupnpd.conf
else
	sed -i -e "s/^uuid=[-0-9a-f]*/uuid=`($(STAGING_DIR_HOST)/bin/genuuid||$(STAGING_DIR_HOST)/bin/uuidgen||$(STAGING_DIR_HOST)/bin/uuid) 2>/dev/null`/" miniupnpd.conf
endif

miniupnpd:	$(BASEOBJS) $(LNXOBJS) 

testupnpdescgen:	$(TESTUPNPDESCGENOBJS)

testgetifstats:	testgetifstats.o linux/getifstats.o

testupnppermissions:	testupnppermissions.o upnppermissions.o

testgetifaddr:	testgetifaddr.o getifaddr.o

testgetroute:	testgetroute.o linux/getroute.o upnputils.o 

miniupnpdctl:	miniupnpdctl.o

config.h:	genconfig.sh VERSION
	./genconfig.sh $(CONFIG_OPTIONS)

depend:	config.h
	makedepend -f$(MAKEFILE_LIST) -Y \
	$(ALLOBJS:.o=.c) $(TESTUPNPDESCGENOBJS:.o=.c) \
	testgetifstats.c 2>/dev/null

# DO NOT DELETE

miniupnpd.o: config.h macros.h upnpglobalvars.h upnppermissions.h
miniupnpd.o: miniupnpdtypes.h upnphttp.h upnpdescgen.h miniupnpdpath.h
miniupnpd.o: getifaddr.h upnpsoap.h options.h minissdp.h upnpredirect.h
miniupnpd.o: upnppinhole.h daemonize.h upnpevents.h commonrdr.h
miniupnpd.o: upnputils.h ifacewatcher.h
upnphttp.o: config.h upnphttp.h upnpdescgen.h miniupnpdpath.h upnpsoap.h
upnphttp.o: upnpevents.h upnputils.h
upnpdescgen.o: config.h getifaddr.h upnpredirect.h upnpdescgen.h
upnpdescgen.o: miniupnpdpath.h upnpglobalvars.h upnppermissions.h
upnpdescgen.o: miniupnpdtypes.h upnpdescstrings.h upnpurns.h getconnstatus.h
upnpsoap.o: macros.h config.h upnpglobalvars.h upnppermissions.h
upnpsoap.o: miniupnpdtypes.h upnphttp.h upnpsoap.h upnpreplyparse.h
upnpsoap.o: upnpredirect.h upnppinhole.h getifaddr.h getifstats.h
upnpsoap.o: getconnstatus.h upnpurns.h
upnpreplyparse.o: upnpreplyparse.h minixml.h
minixml.o: minixml.h
upnpredirect.o: macros.h config.h upnpredirect.h upnpglobalvars.h
upnpredirect.o: upnppermissions.h miniupnpdtypes.h upnpevents.h
upnpredirect.o: netfilter/iptcrdr.h commonrdr.h
getifaddr.o: config.h getifaddr.h
daemonize.o: daemonize.h config.h
upnpglobalvars.o: config.h upnpglobalvars.h upnppermissions.h
upnpglobalvars.o: miniupnpdtypes.h
options.o: options.h config.h upnppermissions.h upnpglobalvars.h
options.o: miniupnpdtypes.h
upnppermissions.o: config.h upnppermissions.h
minissdp.o: config.h upnpdescstrings.h miniupnpdpath.h upnphttp.h
minissdp.o: upnpglobalvars.h upnppermissions.h miniupnpdtypes.h minissdp.h
minissdp.o: upnputils.h getroute.h codelength.h
natpmp.o: macros.h config.h natpmp.h upnpglobalvars.h upnppermissions.h
natpmp.o: miniupnpdtypes.h getifaddr.h upnpredirect.h commonrdr.h upnputils.h
upnpevents.o: config.h upnpevents.h miniupnpdpath.h upnpglobalvars.h
upnpevents.o: upnppermissions.h miniupnpdtypes.h upnpdescgen.h upnputils.h
upnputils.o: config.h upnputils.h
getconnstatus.o: getconnstatus.h getifaddr.h
upnppinhole.o: macros.h config.h upnpredirect.h upnpglobalvars.h
upnppinhole.o: upnppermissions.h miniupnpdtypes.h upnpevents.h
upnppinhole.o: netfilter/iptpinhole.h
linux/getifstats.o: config.h getifstats.h
linux/ifacewatcher.o: config.h ifacewatcher.h config.h minissdp.h
linux/ifacewatcher.o: miniupnpdtypes.h getifaddr.h upnpglobalvars.h
linux/ifacewatcher.o: upnppermissions.h 
linux/getroute.o: getroute.h upnputils.h
netfilter/iptcrdr.o: macros.h config.h netfilter/iptcrdr.h commonrdr.h
netfilter/iptcrdr.o: config.h upnpglobalvars.h upnppermissions.h
netfilter/iptcrdr.o: miniupnpdtypes.h
netfilter/iptpinhole.o: config.h netfilter/iptpinhole.h upnpglobalvars.h
netfilter/iptpinhole.o: upnppermissions.h config.h miniupnpdtypes.h
netfilter/iptpinhole.o: netfilter/tiny_nf_nat.h
testupnpdescgen.o: macros.h config.h upnpdescgen.h
upnpdescgen.o: config.h getifaddr.h upnpredirect.h upnpdescgen.h
upnpdescgen.o: miniupnpdpath.h upnpglobalvars.h upnppermissions.h
upnpdescgen.o: miniupnpdtypes.h upnpdescstrings.h upnpurns.h getconnstatus.h
testgetifstats.o: getifstats.h
