/*     */ package oshi.software.os;
/*     */ 
/*     */ import oshi.annotation.concurrent.ThreadSafe;
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
/*     */ public interface InternetProtocolStats
/*     */ {
/*     */   TcpStats getTCPv4Stats();
/*     */   
/*     */   TcpStats getTCPv6Stats();
/*     */   
/*     */   UdpStats getUDPv4Stats();
/*     */   
/*     */   UdpStats getUDPv6Stats();
/*     */   
/*     */   public static final class TcpStats
/*     */   {
/*     */     private final long connectionsEstablished;
/*     */     private final long connectionsActive;
/*     */     private final long connectionsPassive;
/*     */     private final long connectionFailures;
/*     */     private final long connectionsReset;
/*     */     private final long segmentsSent;
/*     */     private final long segmentsReceived;
/*     */     private final long segmentsRetransmitted;
/*     */     private final long inErrors;
/*     */     private final long outResets;
/*     */     
/*     */     public TcpStats(long connectionsEstablished, long connectionsActive, long connectionsPassive, long connectionFailures, long connectionsReset, long segmentsSent, long segmentsReceived, long segmentsRetransmitted, long inErrors, long outResets) {
/*  82 */       this.connectionsEstablished = connectionsEstablished;
/*  83 */       this.connectionsActive = connectionsActive;
/*  84 */       this.connectionsPassive = connectionsPassive;
/*  85 */       this.connectionFailures = connectionFailures;
/*  86 */       this.connectionsReset = connectionsReset;
/*  87 */       this.segmentsSent = segmentsSent;
/*  88 */       this.segmentsReceived = segmentsReceived;
/*  89 */       this.segmentsRetransmitted = segmentsRetransmitted;
/*  90 */       this.inErrors = inErrors;
/*  91 */       this.outResets = outResets;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public long getConnectionsEstablished() {
/* 101 */       return this.connectionsEstablished;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public long getConnectionsActive() {
/* 113 */       return this.connectionsActive;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public long getConnectionsPassive() {
/* 125 */       return this.connectionsPassive;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public long getConnectionFailures() {
/* 137 */       return this.connectionFailures;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public long getConnectionsReset() {
/* 148 */       return this.connectionsReset;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public long getSegmentsSent() {
/* 158 */       return this.segmentsSent;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public long getSegmentsReceived() {
/* 169 */       return this.segmentsReceived;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public long getSegmentsRetransmitted() {
/* 179 */       return this.segmentsRetransmitted;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public long getInErrors() {
/* 188 */       return this.inErrors;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public long getOutResets() {
/* 197 */       return this.outResets;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 202 */       return "TcpStats [connectionsEstablished=" + this.connectionsEstablished + ", connectionsActive=" + this.connectionsActive + ", connectionsPassive=" + this.connectionsPassive + ", connectionFailures=" + this.connectionFailures + ", connectionsReset=" + this.connectionsReset + ", segmentsSent=" + this.segmentsSent + ", segmentsReceived=" + this.segmentsReceived + ", segmentsRetransmitted=" + this.segmentsRetransmitted + ", inErrors=" + this.inErrors + ", outResets=" + this.outResets + "]";
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static final class UdpStats
/*     */   {
/*     */     private final long datagramsSent;
/*     */     
/*     */     private final long datagramsReceived;
/*     */     
/*     */     private final long datagramsNoPort;
/*     */     
/*     */     private final long datagramsReceivedErrors;
/*     */     
/*     */     public UdpStats(long datagramsSent, long datagramsReceived, long datagramsNoPort, long datagramsReceivedErrors) {
/* 218 */       this.datagramsSent = datagramsSent;
/* 219 */       this.datagramsReceived = datagramsReceived;
/* 220 */       this.datagramsNoPort = datagramsNoPort;
/* 221 */       this.datagramsReceivedErrors = datagramsReceivedErrors;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public long getDatagramsSent() {
/* 230 */       return this.datagramsSent;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public long getDatagramsReceived() {
/* 239 */       return this.datagramsReceived;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public long getDatagramsNoPort() {
/* 249 */       return this.datagramsNoPort;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public long getDatagramsReceivedErrors() {
/* 260 */       return this.datagramsReceivedErrors;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 265 */       return "UdpStats [datagramsSent=" + this.datagramsSent + ", datagramsReceived=" + this.datagramsReceived + ", datagramsNoPort=" + this.datagramsNoPort + ", datagramsReceivedErrors=" + this.datagramsReceivedErrors + "]";
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\software\os\InternetProtocolStats.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */