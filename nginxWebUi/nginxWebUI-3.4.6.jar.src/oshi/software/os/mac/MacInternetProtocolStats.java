/*     */ package oshi.software.os.mac;
/*     */ 
/*     */ import com.sun.jna.Memory;
/*     */ import java.util.function.Supplier;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.driver.unix.NetStatTcp;
/*     */ import oshi.jna.platform.unix.CLibrary;
/*     */ import oshi.software.os.InternetProtocolStats;
/*     */ import oshi.util.Memoizer;
/*     */ import oshi.util.ParseUtil;
/*     */ import oshi.util.platform.mac.SysctlUtil;
/*     */ import oshi.util.tuples.Pair;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @ThreadSafe
/*     */ public class MacInternetProtocolStats
/*     */   implements InternetProtocolStats
/*     */ {
/*     */   private boolean isElevated;
/*     */   private Supplier<Pair<Long, Long>> establishedv4v6;
/*     */   private Supplier<CLibrary.BsdTcpstat> tcpstat;
/*     */   private Supplier<CLibrary.BsdUdpstat> udpstat;
/*     */   private Supplier<CLibrary.BsdIpstat> ipstat;
/*     */   private Supplier<CLibrary.BsdIp6stat> ip6stat;
/*     */   
/*     */   public MacInternetProtocolStats(boolean elevated) {
/*  53 */     this.establishedv4v6 = Memoizer.memoize(NetStatTcp::queryTcpnetstat, Memoizer.defaultExpiration());
/*  54 */     this.tcpstat = Memoizer.memoize(MacInternetProtocolStats::queryTcpstat, Memoizer.defaultExpiration());
/*  55 */     this.udpstat = Memoizer.memoize(MacInternetProtocolStats::queryUdpstat, Memoizer.defaultExpiration());
/*     */ 
/*     */     
/*  58 */     this.ipstat = Memoizer.memoize(MacInternetProtocolStats::queryIpstat, Memoizer.defaultExpiration());
/*  59 */     this.ip6stat = Memoizer.memoize(MacInternetProtocolStats::queryIp6stat, Memoizer.defaultExpiration());
/*     */     this.isElevated = elevated;
/*     */   }
/*     */   public InternetProtocolStats.TcpStats getTCPv4Stats() {
/*  63 */     CLibrary.BsdTcpstat tcp = this.tcpstat.get();
/*  64 */     if (this.isElevated) {
/*  65 */       return new InternetProtocolStats.TcpStats(((Long)((Pair)this.establishedv4v6.get()).getA()).longValue(), ParseUtil.unsignedIntToLong(tcp.tcps_connattempt), 
/*  66 */           ParseUtil.unsignedIntToLong(tcp.tcps_accepts), ParseUtil.unsignedIntToLong(tcp.tcps_conndrops), 
/*  67 */           ParseUtil.unsignedIntToLong(tcp.tcps_drops), ParseUtil.unsignedIntToLong(tcp.tcps_sndpack), 
/*  68 */           ParseUtil.unsignedIntToLong(tcp.tcps_rcvpack), ParseUtil.unsignedIntToLong(tcp.tcps_sndrexmitpack), 
/*  69 */           ParseUtil.unsignedIntToLong(tcp.tcps_rcvbadsum + tcp.tcps_rcvbadoff + tcp.tcps_rcvmemdrop + tcp.tcps_rcvshort), 0L);
/*     */     }
/*     */ 
/*     */     
/*  73 */     CLibrary.BsdIpstat ip = this.ipstat.get();
/*  74 */     CLibrary.BsdUdpstat udp = this.udpstat.get();
/*  75 */     return new InternetProtocolStats.TcpStats(((Long)((Pair)this.establishedv4v6.get()).getA()).longValue(), ParseUtil.unsignedIntToLong(tcp.tcps_connattempt), 
/*  76 */         ParseUtil.unsignedIntToLong(tcp.tcps_accepts), ParseUtil.unsignedIntToLong(tcp.tcps_conndrops), 
/*  77 */         ParseUtil.unsignedIntToLong(tcp.tcps_drops), 
/*  78 */         Math.max(0L, ParseUtil.unsignedIntToLong(ip.ips_delivered - udp.udps_opackets)), 
/*  79 */         Math.max(0L, ParseUtil.unsignedIntToLong(ip.ips_total - udp.udps_ipackets)), 
/*  80 */         ParseUtil.unsignedIntToLong(tcp.tcps_sndrexmitpack), 
/*  81 */         Math.max(0L, ParseUtil.unsignedIntToLong(ip.ips_badsum + ip.ips_tooshort + ip.ips_toosmall + ip.ips_badhlen + ip.ips_badlen - udp.udps_hdrops + udp.udps_badsum + udp.udps_badlen)), 0L);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public InternetProtocolStats.TcpStats getTCPv6Stats() {
/*  88 */     CLibrary.BsdIp6stat ip6 = this.ip6stat.get();
/*  89 */     CLibrary.BsdUdpstat udp = this.udpstat.get();
/*  90 */     return new InternetProtocolStats.TcpStats(((Long)((Pair)this.establishedv4v6.get()).getB()).longValue(), 0L, 0L, 0L, 0L, ip6.ip6s_localout - 
/*  91 */         ParseUtil.unsignedIntToLong(udp.udps_snd6_swcsum), ip6.ip6s_total - 
/*  92 */         ParseUtil.unsignedIntToLong(udp.udps_rcv6_swcsum), 0L, 0L, 0L);
/*     */   }
/*     */ 
/*     */   
/*     */   public InternetProtocolStats.UdpStats getUDPv4Stats() {
/*  97 */     CLibrary.BsdUdpstat stat = this.udpstat.get();
/*  98 */     return new InternetProtocolStats.UdpStats(ParseUtil.unsignedIntToLong(stat.udps_opackets), 
/*  99 */         ParseUtil.unsignedIntToLong(stat.udps_ipackets), ParseUtil.unsignedIntToLong(stat.udps_noportmcast), 
/* 100 */         ParseUtil.unsignedIntToLong(stat.udps_hdrops + stat.udps_badsum + stat.udps_badlen));
/*     */   }
/*     */ 
/*     */   
/*     */   public InternetProtocolStats.UdpStats getUDPv6Stats() {
/* 105 */     CLibrary.BsdUdpstat stat = this.udpstat.get();
/* 106 */     return new InternetProtocolStats.UdpStats(ParseUtil.unsignedIntToLong(stat.udps_snd6_swcsum), 
/* 107 */         ParseUtil.unsignedIntToLong(stat.udps_rcv6_swcsum), 0L, 0L);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static CLibrary.BsdTcpstat queryTcpstat() {
/* 118 */     CLibrary.BsdTcpstat mt = new CLibrary.BsdTcpstat();
/* 119 */     Memory m = SysctlUtil.sysctl("net.inet.tcp.stats");
/* 120 */     if (m != null && m.size() >= 128L) {
/* 121 */       mt.tcps_connattempt = m.getInt(0L);
/* 122 */       mt.tcps_accepts = m.getInt(4L);
/* 123 */       mt.tcps_drops = m.getInt(12L);
/* 124 */       mt.tcps_conndrops = m.getInt(16L);
/* 125 */       mt.tcps_sndpack = m.getInt(64L);
/* 126 */       mt.tcps_sndrexmitpack = m.getInt(72L);
/* 127 */       mt.tcps_rcvpack = m.getInt(104L);
/* 128 */       mt.tcps_rcvbadsum = m.getInt(112L);
/* 129 */       mt.tcps_rcvbadoff = m.getInt(116L);
/* 130 */       mt.tcps_rcvmemdrop = m.getInt(120L);
/* 131 */       mt.tcps_rcvshort = m.getInt(124L);
/*     */     } 
/* 133 */     return mt;
/*     */   }
/*     */   
/*     */   private static CLibrary.BsdIpstat queryIpstat() {
/* 137 */     CLibrary.BsdIpstat mi = new CLibrary.BsdIpstat();
/* 138 */     Memory m = SysctlUtil.sysctl("net.inet.ip.stats");
/* 139 */     if (m != null && m.size() >= 60L) {
/* 140 */       mi.ips_total = m.getInt(0L);
/* 141 */       mi.ips_badsum = m.getInt(4L);
/* 142 */       mi.ips_tooshort = m.getInt(8L);
/* 143 */       mi.ips_toosmall = m.getInt(12L);
/* 144 */       mi.ips_badhlen = m.getInt(16L);
/* 145 */       mi.ips_badlen = m.getInt(20L);
/* 146 */       mi.ips_delivered = m.getInt(56L);
/*     */     } 
/* 148 */     return mi;
/*     */   }
/*     */   
/*     */   private static CLibrary.BsdIp6stat queryIp6stat() {
/* 152 */     CLibrary.BsdIp6stat mi6 = new CLibrary.BsdIp6stat();
/* 153 */     Memory m = SysctlUtil.sysctl("net.inet6.ip6.stats");
/* 154 */     if (m != null && m.size() >= 96L) {
/* 155 */       mi6.ip6s_total = m.getLong(0L);
/* 156 */       mi6.ip6s_localout = m.getLong(88L);
/*     */     } 
/* 158 */     return mi6;
/*     */   }
/*     */   
/*     */   public static CLibrary.BsdUdpstat queryUdpstat() {
/* 162 */     CLibrary.BsdUdpstat ut = new CLibrary.BsdUdpstat();
/* 163 */     Memory m = SysctlUtil.sysctl("net.inet.udp.stats");
/* 164 */     if (m != null && m.size() >= 1644L) {
/* 165 */       ut.udps_ipackets = m.getInt(0L);
/* 166 */       ut.udps_hdrops = m.getInt(4L);
/* 167 */       ut.udps_badsum = m.getInt(8L);
/* 168 */       ut.udps_badlen = m.getInt(12L);
/* 169 */       ut.udps_opackets = m.getInt(36L);
/* 170 */       ut.udps_noportmcast = m.getInt(48L);
/* 171 */       ut.udps_rcv6_swcsum = m.getInt(64L);
/* 172 */       ut.udps_snd6_swcsum = m.getInt(80L);
/*     */     } 
/* 174 */     return ut;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\software\os\mac\MacInternetProtocolStats.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */