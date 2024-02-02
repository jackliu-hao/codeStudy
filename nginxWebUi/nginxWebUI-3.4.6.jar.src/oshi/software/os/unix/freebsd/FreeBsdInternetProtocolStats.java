/*     */ package oshi.software.os.unix.freebsd;
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
/*     */ public class FreeBsdInternetProtocolStats
/*     */   implements InternetProtocolStats
/*     */ {
/*  45 */   private Supplier<Pair<Long, Long>> establishedv4v6 = Memoizer.memoize(NetStatTcp::queryTcpnetstat, Memoizer.defaultExpiration());
/*  46 */   private Supplier<CLibrary.BsdTcpstat> tcpstat = Memoizer.memoize(FreeBsdInternetProtocolStats::queryTcpstat, Memoizer.defaultExpiration());
/*  47 */   private Supplier<CLibrary.BsdUdpstat> udpstat = Memoizer.memoize(FreeBsdInternetProtocolStats::queryUdpstat, Memoizer.defaultExpiration());
/*     */ 
/*     */   
/*     */   public InternetProtocolStats.TcpStats getTCPv4Stats() {
/*  51 */     CLibrary.BsdTcpstat tcp = this.tcpstat.get();
/*  52 */     return new InternetProtocolStats.TcpStats(((Long)((Pair)this.establishedv4v6.get()).getA()).longValue(), ParseUtil.unsignedIntToLong(tcp.tcps_connattempt), 
/*  53 */         ParseUtil.unsignedIntToLong(tcp.tcps_accepts), ParseUtil.unsignedIntToLong(tcp.tcps_conndrops), 
/*  54 */         ParseUtil.unsignedIntToLong(tcp.tcps_drops), ParseUtil.unsignedIntToLong(tcp.tcps_sndpack), 
/*  55 */         ParseUtil.unsignedIntToLong(tcp.tcps_rcvpack), ParseUtil.unsignedIntToLong(tcp.tcps_sndrexmitpack), 
/*  56 */         ParseUtil.unsignedIntToLong(tcp.tcps_rcvbadsum + tcp.tcps_rcvbadoff + tcp.tcps_rcvmemdrop + tcp.tcps_rcvshort), 0L);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public InternetProtocolStats.TcpStats getTCPv6Stats() {
/*  63 */     return new InternetProtocolStats.TcpStats(((Long)((Pair)this.establishedv4v6.get()).getB()).longValue(), 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L);
/*     */   }
/*     */ 
/*     */   
/*     */   public InternetProtocolStats.UdpStats getUDPv4Stats() {
/*  68 */     CLibrary.BsdUdpstat stat = this.udpstat.get();
/*  69 */     return new InternetProtocolStats.UdpStats(ParseUtil.unsignedIntToLong(stat.udps_opackets), 
/*  70 */         ParseUtil.unsignedIntToLong(stat.udps_ipackets), ParseUtil.unsignedIntToLong(stat.udps_noportmcast), 
/*  71 */         ParseUtil.unsignedIntToLong(stat.udps_hdrops + stat.udps_badsum + stat.udps_badlen));
/*     */   }
/*     */ 
/*     */   
/*     */   public InternetProtocolStats.UdpStats getUDPv6Stats() {
/*  76 */     CLibrary.BsdUdpstat stat = this.udpstat.get();
/*  77 */     return new InternetProtocolStats.UdpStats(ParseUtil.unsignedIntToLong(stat.udps_snd6_swcsum), 
/*  78 */         ParseUtil.unsignedIntToLong(stat.udps_rcv6_swcsum), 0L, 0L);
/*     */   }
/*     */   
/*     */   private static CLibrary.BsdTcpstat queryTcpstat() {
/*  82 */     CLibrary.BsdTcpstat ft = new CLibrary.BsdTcpstat();
/*  83 */     Memory m = SysctlUtil.sysctl("net.inet.tcp.stats");
/*  84 */     if (m != null && m.size() >= 128L) {
/*  85 */       ft.tcps_connattempt = m.getInt(0L);
/*  86 */       ft.tcps_accepts = m.getInt(4L);
/*  87 */       ft.tcps_drops = m.getInt(12L);
/*  88 */       ft.tcps_conndrops = m.getInt(16L);
/*  89 */       ft.tcps_sndpack = m.getInt(64L);
/*  90 */       ft.tcps_sndrexmitpack = m.getInt(72L);
/*  91 */       ft.tcps_rcvpack = m.getInt(104L);
/*  92 */       ft.tcps_rcvbadsum = m.getInt(112L);
/*  93 */       ft.tcps_rcvbadoff = m.getInt(116L);
/*  94 */       ft.tcps_rcvmemdrop = m.getInt(120L);
/*  95 */       ft.tcps_rcvshort = m.getInt(124L);
/*     */     } 
/*  97 */     return ft;
/*     */   }
/*     */   
/*     */   private static CLibrary.BsdUdpstat queryUdpstat() {
/* 101 */     CLibrary.BsdUdpstat ut = new CLibrary.BsdUdpstat();
/* 102 */     Memory m = SysctlUtil.sysctl("net.inet.udp.stats");
/* 103 */     if (m != null && m.size() >= 1644L) {
/* 104 */       ut.udps_ipackets = m.getInt(0L);
/* 105 */       ut.udps_hdrops = m.getInt(4L);
/* 106 */       ut.udps_badsum = m.getInt(8L);
/* 107 */       ut.udps_badlen = m.getInt(12L);
/* 108 */       ut.udps_opackets = m.getInt(36L);
/* 109 */       ut.udps_noportmcast = m.getInt(48L);
/* 110 */       ut.udps_rcv6_swcsum = m.getInt(64L);
/* 111 */       ut.udps_snd6_swcsum = m.getInt(80L);
/*     */     } 
/* 113 */     return ut;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\software\o\\unix\freebsd\FreeBsdInternetProtocolStats.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */