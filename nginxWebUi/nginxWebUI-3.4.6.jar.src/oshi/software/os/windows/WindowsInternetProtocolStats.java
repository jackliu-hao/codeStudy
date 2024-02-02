/*    */ package oshi.software.os.windows;
/*    */ 
/*    */ import com.sun.jna.platform.win32.IPHlpAPI;
/*    */ import oshi.annotation.concurrent.ThreadSafe;
/*    */ import oshi.software.os.InternetProtocolStats;
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
/*    */ public class WindowsInternetProtocolStats
/*    */   implements InternetProtocolStats
/*    */ {
/* 36 */   private static final IPHlpAPI IPHLP = IPHlpAPI.INSTANCE;
/*    */ 
/*    */   
/*    */   public InternetProtocolStats.TcpStats getTCPv4Stats() {
/* 40 */     IPHlpAPI.MIB_TCPSTATS stats = new IPHlpAPI.MIB_TCPSTATS();
/* 41 */     IPHLP.GetTcpStatisticsEx(stats, 2);
/* 42 */     return new InternetProtocolStats.TcpStats(stats.dwCurrEstab, stats.dwActiveOpens, stats.dwPassiveOpens, stats.dwAttemptFails, stats.dwEstabResets, stats.dwOutSegs, stats.dwInSegs, stats.dwRetransSegs, stats.dwInErrs, stats.dwOutRsts);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public InternetProtocolStats.TcpStats getTCPv6Stats() {
/* 49 */     IPHlpAPI.MIB_TCPSTATS stats = new IPHlpAPI.MIB_TCPSTATS();
/* 50 */     IPHLP.GetTcpStatisticsEx(stats, 23);
/* 51 */     return new InternetProtocolStats.TcpStats(stats.dwCurrEstab, stats.dwActiveOpens, stats.dwPassiveOpens, stats.dwAttemptFails, stats.dwEstabResets, stats.dwOutSegs, stats.dwInSegs, stats.dwRetransSegs, stats.dwInErrs, stats.dwOutRsts);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public InternetProtocolStats.UdpStats getUDPv4Stats() {
/* 58 */     IPHlpAPI.MIB_UDPSTATS stats = new IPHlpAPI.MIB_UDPSTATS();
/* 59 */     IPHLP.GetUdpStatisticsEx(stats, 2);
/* 60 */     return new InternetProtocolStats.UdpStats(stats.dwOutDatagrams, stats.dwInDatagrams, stats.dwNoPorts, stats.dwInErrors);
/*    */   }
/*    */ 
/*    */   
/*    */   public InternetProtocolStats.UdpStats getUDPv6Stats() {
/* 65 */     IPHlpAPI.MIB_UDPSTATS stats = new IPHlpAPI.MIB_UDPSTATS();
/* 66 */     IPHLP.GetUdpStatisticsEx(stats, 23);
/* 67 */     return new InternetProtocolStats.UdpStats(stats.dwOutDatagrams, stats.dwInDatagrams, stats.dwNoPorts, stats.dwInErrors);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\software\os\windows\WindowsInternetProtocolStats.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */