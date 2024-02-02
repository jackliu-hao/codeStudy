/*     */ package oshi.software.os.unix.solaris;
/*     */ 
/*     */ import java.util.List;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.software.os.InternetProtocolStats;
/*     */ import oshi.util.ExecutingCommand;
/*     */ import oshi.util.ParseUtil;
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
/*     */ public class SolarisInternetProtocolStats
/*     */   implements InternetProtocolStats
/*     */ {
/*     */   public InternetProtocolStats.TcpStats getTCPv4Stats() {
/*  38 */     return getTcpStats();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public InternetProtocolStats.TcpStats getTCPv6Stats() {
/*  44 */     return new InternetProtocolStats.TcpStats(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L);
/*     */   }
/*     */ 
/*     */   
/*     */   public InternetProtocolStats.UdpStats getUDPv4Stats() {
/*  49 */     return getUdpStats();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public InternetProtocolStats.UdpStats getUDPv6Stats() {
/*  55 */     return new InternetProtocolStats.UdpStats(0L, 0L, 0L, 0L);
/*     */   }
/*     */   
/*     */   private static InternetProtocolStats.TcpStats getTcpStats() {
/*  59 */     long connectionsEstablished = 0L;
/*  60 */     long connectionsActive = 0L;
/*  61 */     long connectionsPassive = 0L;
/*  62 */     long connectionFailures = 0L;
/*  63 */     long connectionsReset = 0L;
/*  64 */     long segmentsSent = 0L;
/*  65 */     long segmentsReceived = 0L;
/*  66 */     long segmentsRetransmitted = 0L;
/*  67 */     long inErrors = 0L;
/*  68 */     long outResets = 0L;
/*  69 */     List<String> netstat = ExecutingCommand.runNative("netstat -s -P tcp");
/*     */     
/*  71 */     netstat.addAll(ExecutingCommand.runNative("netstat -s -P ip"));
/*  72 */     for (String s : netstat) {
/*     */       
/*  74 */       String[] stats = splitOnPrefix(s, "tcp");
/*     */       
/*  76 */       for (String stat : stats) {
/*  77 */         if (stat != null) {
/*  78 */           String[] split = stat.split("=");
/*  79 */           if (split.length == 2) {
/*  80 */             switch (split[0].trim()) {
/*     */               case "tcpCurrEstab":
/*  82 */                 connectionsEstablished = ParseUtil.parseLongOrDefault(split[1].trim(), 0L);
/*     */                 break;
/*     */               case "tcpActiveOpens":
/*  85 */                 connectionsActive = ParseUtil.parseLongOrDefault(split[1].trim(), 0L);
/*     */                 break;
/*     */               case "tcpPassiveOpens":
/*  88 */                 connectionsPassive = ParseUtil.parseLongOrDefault(split[1].trim(), 0L);
/*     */                 break;
/*     */               case "tcpAttemptFails":
/*  91 */                 connectionFailures = ParseUtil.parseLongOrDefault(split[1].trim(), 0L);
/*     */                 break;
/*     */               case "tcpEstabResets":
/*  94 */                 connectionsReset = ParseUtil.parseLongOrDefault(split[1].trim(), 0L);
/*     */                 break;
/*     */               case "tcpOutSegs":
/*  97 */                 segmentsSent = ParseUtil.parseLongOrDefault(split[1].trim(), 0L);
/*     */                 break;
/*     */               case "tcpInSegs":
/* 100 */                 segmentsReceived = ParseUtil.parseLongOrDefault(split[1].trim(), 0L);
/*     */                 break;
/*     */               case "tcpRetransSegs":
/* 103 */                 segmentsRetransmitted = ParseUtil.parseLongOrDefault(split[1].trim(), 0L);
/*     */                 break;
/*     */               
/*     */               case "tcpInErr":
/* 107 */                 inErrors = ParseUtil.getFirstIntValue(split[1].trim());
/*     */                 break;
/*     */               case "tcpOutRsts":
/* 110 */                 outResets = ParseUtil.parseLongOrDefault(split[1].trim(), 0L);
/*     */                 break;
/*     */             } 
/*     */ 
/*     */           
/*     */           }
/*     */         } 
/*     */       } 
/*     */     } 
/* 119 */     return new InternetProtocolStats.TcpStats(connectionsEstablished, connectionsActive, connectionsPassive, connectionFailures, connectionsReset, segmentsSent, segmentsReceived, segmentsRetransmitted, inErrors, outResets);
/*     */   }
/*     */ 
/*     */   
/*     */   private static InternetProtocolStats.UdpStats getUdpStats() {
/* 124 */     long datagramsSent = 0L;
/* 125 */     long datagramsReceived = 0L;
/* 126 */     long datagramsNoPort = 0L;
/* 127 */     long datagramsReceivedErrors = 0L;
/* 128 */     List<String> netstat = ExecutingCommand.runNative("netstat -s -P udp");
/*     */     
/* 130 */     netstat.addAll(ExecutingCommand.runNative("netstat -s -P ip"));
/* 131 */     for (String s : netstat) {
/*     */       
/* 133 */       String[] stats = splitOnPrefix(s, "udp");
/*     */       
/* 135 */       for (String stat : stats) {
/* 136 */         if (stat != null) {
/* 137 */           String[] split = stat.split("=");
/* 138 */           if (split.length == 2) {
/* 139 */             switch (split[0].trim()) {
/*     */               case "udpOutDatagrams":
/* 141 */                 datagramsSent = ParseUtil.parseLongOrDefault(split[1].trim(), 0L);
/*     */                 break;
/*     */               case "udpInDatagrams":
/* 144 */                 datagramsReceived = ParseUtil.parseLongOrDefault(split[1].trim(), 0L);
/*     */                 break;
/*     */               case "udpNoPorts":
/* 147 */                 datagramsNoPort = ParseUtil.parseLongOrDefault(split[1].trim(), 0L);
/*     */                 break;
/*     */               case "udpInErrors":
/* 150 */                 datagramsReceivedErrors = ParseUtil.parseLongOrDefault(split[1].trim(), 0L);
/*     */                 break;
/*     */             } 
/*     */ 
/*     */           
/*     */           }
/*     */         } 
/*     */       } 
/*     */     } 
/* 159 */     return new InternetProtocolStats.UdpStats(datagramsSent, datagramsReceived, datagramsNoPort, datagramsReceivedErrors);
/*     */   }
/*     */   
/*     */   private static String[] splitOnPrefix(String s, String prefix) {
/* 163 */     String[] stats = new String[2];
/* 164 */     int first = s.indexOf(prefix);
/* 165 */     if (first >= 0) {
/* 166 */       int second = s.indexOf(prefix, first + 1);
/* 167 */       if (second >= 0) {
/* 168 */         stats[0] = s.substring(first, second).trim();
/* 169 */         stats[1] = s.substring(second).trim();
/*     */       } else {
/* 171 */         stats[0] = s.substring(first).trim();
/*     */       } 
/*     */     } 
/* 174 */     return stats;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\software\o\\unix\solaris\SolarisInternetProtocolStats.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */