/* MiniUPnP Project
 * http://miniupnp.free.fr/ or http://miniupnp.tuxfamily.org/
 * (c) 2006-2012 Thomas Bernard
 * generated by ./genconfig.sh on Mon Apr 21 22:11:51 PDT 2014
 * using command line options  */
#ifndef CONFIG_H_INCLUDED
#define CONFIG_H_INCLUDED

#include <inttypes.h>

#define MINIUPNPD_VERSION "1.8"

#define UPNP_VERSION	"20140421"
#define USE_IFACEWATCHER 1
#define USE_NETFILTER 0
#define SUPPORT_REMOTEHOST

#define OS_NAME		"Linux - XONET"
#define OS_VERSION	"Linux/3.13.7-100.armhf"
#define OS_URL		"http://www.xoware.com/"

/* syslog facility to be used by miniupnpd */
#define LOG_MINIUPNPD		 LOG_DAEMON

/* Uncomment the following line to allow miniupnpd to be
 * controlled by miniupnpdctl */
/*#define USE_MINIUPNPDCTL*/

/* Comment the following line to disable NAT-PMP operations */
//#define ENABLE_NATPMP

/* Uncomment the following line to enable generation of
 * filter rules with pf */
/*#define PF_ENABLE_FILTER_RULES*/

/* Uncomment the following line to enable caching of results of
 * the getifstats() function */
/*#define ENABLE_GETIFSTATS_CACHING*/
/* The cache duration is indicated in seconds */
#define GETIFSTATS_CACHING_DURATION 2

/* Uncomment the following line to enable multiple external ip support */
/* note : That is EXPERIMENTAL, do not use that unless you know perfectly what you are doing */
/* Dynamic external ip adresses are not supported when this option is enabled.
 * Also note that you would need to configure your .conf file accordingly. */
/*#define MULTIPLE_EXTERNAL_IP*/

/* Comment the following line to use home made daemonize() func instead
 * of BSD daemon() */
#define USE_DAEMON

/* Uncomment the following line to enable lease file support */
/*#define ENABLE_LEASEFILE*/

/* Define one or none of the two following macros in order to make some
 * clients happy. It will change the XML Root Description of the IGD.
 * Enabling the Layer3Forwarding Service seems to be the more compatible
 * option. */
/*#define HAS_DUMMY_SERVICE*/
#define ENABLE_L3F_SERVICE

/* Enable IP v6 support */
/*#define ENABLE_IPV6*/

/* Enable the support of IGD v2 specification.
 * This is not fully tested yet and can cause incompatibilities with some
 * control points, so enable with care. */
/*#define IGD_V2*/

#ifdef IGD_V2
/* Enable DeviceProtection service (IGDv2) */
#define ENABLE_DP_SERVICE

/* Enable WANIPv6FirewallControl service (IGDv2). needs IPv6 */
#ifdef ENABLE_IPV6
#define ENABLE_6FC_SERVICE
#endif /* ENABLE_IPV6 */
#endif /* IGD_V2 */

/* UPnP Events support. Working well enough to be enabled by default.
 * It can be disabled to save a few bytes. */
//#define ENABLE_EVENTS

/* include interface name in pf and ipf rules */
//#define USE_IFNAME_IN_RULES

/* Experimental NFQUEUE support. */
/*#define ENABLE_NFQUEUE*/

/* Enable to make MiniUPnPd more strict about UPnP conformance
 * and the messages it receives from control points */
/*#define UPNP_STRICT*/

/* Add the optional Date: header in all HTTP responses */
/*#define ENABLE_HTTP_DATE*/

/* disable reading and parsing of config file (miniupnpd.conf) */
/*#define DISABLE_CONFIG_FILE*/

#endif
