/*     */ package oshi.software.os.linux;
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
/*     */ public class LinuxInternetProtocolStats
/*     */   implements InternetProtocolStats
/*     */ {
/*     */   public InternetProtocolStats.TcpStats getTCPv4Stats() {
/*  38 */     return getTcpStats("netstat -st4");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public InternetProtocolStats.TcpStats getTCPv6Stats() {
/*  44 */     return new InternetProtocolStats.TcpStats(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L);
/*     */   }
/*     */   
/*     */   private static InternetProtocolStats.TcpStats getTcpStats(String netstatStr) {
/*  48 */     long connectionsEstablished = 0L;
/*  49 */     long connectionsActive = 0L;
/*  50 */     long connectionsPassive = 0L;
/*  51 */     long connectionFailures = 0L;
/*  52 */     long connectionsReset = 0L;
/*  53 */     long segmentsSent = 0L;
/*  54 */     long segmentsReceived = 0L;
/*  55 */     long segmentsRetransmitted = 0L;
/*  56 */     long inErrors = 0L;
/*  57 */     long outResets = 0L;
/*  58 */     List<String> netstat = ExecutingCommand.runNative(netstatStr);
/*  59 */     for (String s : netstat) {
/*  60 */       String[] split = s.trim().split(" ", 2);
/*  61 */       if (split.length == 2) {
/*  62 */         switch (split[1]) {
/*     */           case "connections established":
/*  64 */             connectionsEstablished = ParseUtil.parseLongOrDefault(split[0], 0L);
/*     */           
/*     */           case "active connection openings":
/*  67 */             connectionsActive = ParseUtil.parseLongOrDefault(split[0], 0L);
/*     */           
/*     */           case "passive connection openings":
/*  70 */             connectionsPassive = ParseUtil.parseLongOrDefault(split[0], 0L);
/*     */           
/*     */           case "failed connection attempts":
/*  73 */             connectionFailures = ParseUtil.parseLongOrDefault(split[0], 0L);
/*     */           
/*     */           case "connection resets received":
/*  76 */             connectionsReset = ParseUtil.parseLongOrDefault(split[0], 0L);
/*     */           
/*     */           case "segments sent out":
/*  79 */             segmentsSent = ParseUtil.parseLongOrDefault(split[0], 0L);
/*     */           
/*     */           case "segments received":
/*  82 */             segmentsReceived = ParseUtil.parseLongOrDefault(split[0], 0L);
/*     */           
/*     */           case "segments retransmitted":
/*  85 */             segmentsRetransmitted = ParseUtil.parseLongOrDefault(split[0], 0L);
/*     */           
/*     */           case "bad segments received":
/*  88 */             inErrors = ParseUtil.parseLongOrDefault(split[0], 0L);
/*     */           
/*     */           case "resets sent":
/*  91 */             outResets = ParseUtil.parseLongOrDefault(split[0], 0L);
/*     */         } 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       }
/*     */     } 
/*  99 */     return new InternetProtocolStats.TcpStats(connectionsEstablished, connectionsActive, connectionsPassive, connectionFailures, connectionsReset, segmentsSent, segmentsReceived, segmentsRetransmitted, inErrors, outResets);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public InternetProtocolStats.UdpStats getUDPv4Stats() {
/* 105 */     return getUdpStats("netstat -su4");
/*     */   }
/*     */ 
/*     */   
/*     */   public InternetProtocolStats.UdpStats getUDPv6Stats() {
/* 110 */     return getUdpStats("netstat -su6");
/*     */   }
/*     */   
/*     */   private static InternetProtocolStats.UdpStats getUdpStats(String netstatStr) {
/* 114 */     long datagramsSent = 0L;
/* 115 */     long datagramsReceived = 0L;
/* 116 */     long datagramsNoPort = 0L;
/* 117 */     long datagramsReceivedErrors = 0L;
/* 118 */     List<String> netstat = ExecutingCommand.runNative(netstatStr);
/* 119 */     for (String s : netstat) {
/* 120 */       String[] split = s.trim().split(" ", 2);
/* 121 */       if (split.length == 2) {
/* 122 */         switch (split[1]) {
/*     */           case "packets sent":
/* 124 */             datagramsSent = ParseUtil.parseLongOrDefault(split[0], 0L);
/*     */           
/*     */           case "packets received":
/* 127 */             datagramsReceived = ParseUtil.parseLongOrDefault(split[0], 0L);
/*     */           
/*     */           case "packets to unknown port received":
/* 130 */             datagramsNoPort = ParseUtil.parseLongOrDefault(split[0], 0L);
/*     */           
/*     */           case "packet receive errors":
/* 133 */             datagramsReceivedErrors = ParseUtil.parseLongOrDefault(split[0], 0L);
/*     */         } 
/*     */ 
/*     */ 
/*     */       
/*     */       }
/*     */     } 
/* 140 */     return new InternetProtocolStats.UdpStats(datagramsSent, datagramsReceived, datagramsNoPort, datagramsReceivedErrors);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\software\os\linux\LinuxInternetProtocolStats.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */