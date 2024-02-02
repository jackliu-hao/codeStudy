/*    */ package oshi.software.os.unix.aix;
/*    */ 
/*    */ import com.sun.jna.Native;
/*    */ import java.util.function.Supplier;
/*    */ import oshi.annotation.concurrent.ThreadSafe;
/*    */ import oshi.driver.unix.aix.perfstat.PerfstatProtocol;
/*    */ import oshi.jna.platform.unix.aix.Perfstat;
/*    */ import oshi.software.os.InternetProtocolStats;
/*    */ import oshi.util.Memoizer;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @ThreadSafe
/*    */ public class AixInternetProtocolStats
/*    */   implements InternetProtocolStats
/*    */ {
/* 41 */   private Supplier<Perfstat.perfstat_protocol_t[]> ipstats = Memoizer.memoize(PerfstatProtocol::queryProtocols, Memoizer.defaultExpiration());
/*    */ 
/*    */   
/*    */   public InternetProtocolStats.TcpStats getTCPv4Stats() {
/* 45 */     for (Perfstat.perfstat_protocol_t stat : (Perfstat.perfstat_protocol_t[])this.ipstats.get()) {
/* 46 */       if ("tcp".equals(Native.toString(stat.name))) {
/* 47 */         return new InternetProtocolStats.TcpStats(stat.u.tcp.established, stat.u.tcp.initiated, stat.u.tcp.accepted, stat.u.tcp.dropped, stat.u.tcp.dropped, stat.u.tcp.opackets, stat.u.tcp.ipackets, 0L, stat.u.tcp.ierrors, 0L);
/*    */       }
/*    */     } 
/*    */ 
/*    */     
/* 52 */     return new InternetProtocolStats.TcpStats(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public InternetProtocolStats.TcpStats getTCPv6Stats() {
/* 58 */     return new InternetProtocolStats.TcpStats(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L);
/*    */   }
/*    */ 
/*    */   
/*    */   public InternetProtocolStats.UdpStats getUDPv4Stats() {
/* 63 */     for (Perfstat.perfstat_protocol_t stat : (Perfstat.perfstat_protocol_t[])this.ipstats.get()) {
/* 64 */       if ("udp".equals(Native.toString(stat.name))) {
/* 65 */         return new InternetProtocolStats.UdpStats(stat.u.udp.opackets, stat.u.udp.ipackets, stat.u.udp.no_socket, stat.u.udp.ierrors);
/*    */       }
/*    */     } 
/* 68 */     return new InternetProtocolStats.UdpStats(0L, 0L, 0L, 0L);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public InternetProtocolStats.UdpStats getUDPv6Stats() {
/* 74 */     return new InternetProtocolStats.UdpStats(0L, 0L, 0L, 0L);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\software\o\\unix\aix\AixInternetProtocolStats.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */