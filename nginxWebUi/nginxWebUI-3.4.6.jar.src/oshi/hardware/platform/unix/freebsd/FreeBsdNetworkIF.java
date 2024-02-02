/*     */ package oshi.hardware.platform.unix.freebsd;
/*     */ 
/*     */ import java.net.NetworkInterface;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.function.Function;
/*     */ import java.util.stream.Collectors;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.hardware.NetworkIF;
/*     */ import oshi.hardware.common.AbstractNetworkIF;
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
/*     */ 
/*     */ @ThreadSafe
/*     */ public final class FreeBsdNetworkIF
/*     */   extends AbstractNetworkIF
/*     */ {
/*     */   private long bytesRecv;
/*     */   private long bytesSent;
/*     */   private long packetsRecv;
/*     */   private long packetsSent;
/*     */   private long inErrors;
/*     */   private long outErrors;
/*     */   private long inDrops;
/*     */   private long collisions;
/*     */   private long timeStamp;
/*     */   
/*     */   public FreeBsdNetworkIF(NetworkInterface netint) {
/*  54 */     super(netint);
/*  55 */     updateAttributes();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static List<NetworkIF> getNetworks(boolean includeLocalInterfaces) {
/*  67 */     return Collections.unmodifiableList((List<? extends NetworkIF>)getNetworkInterfaces(includeLocalInterfaces).stream()
/*  68 */         .map(FreeBsdNetworkIF::new).collect(Collectors.toList()));
/*     */   }
/*     */ 
/*     */   
/*     */   public long getBytesRecv() {
/*  73 */     return this.bytesRecv;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getBytesSent() {
/*  78 */     return this.bytesSent;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getPacketsRecv() {
/*  83 */     return this.packetsRecv;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getPacketsSent() {
/*  88 */     return this.packetsSent;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getInErrors() {
/*  93 */     return this.inErrors;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getOutErrors() {
/*  98 */     return this.outErrors;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getInDrops() {
/* 103 */     return this.inDrops;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getCollisions() {
/* 108 */     return this.collisions;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getSpeed() {
/* 113 */     return 0L;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getTimeStamp() {
/* 118 */     return this.timeStamp;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean updateAttributes() {
/* 123 */     String stats = ExecutingCommand.getAnswerAt("netstat -bI " + getName(), 1);
/* 124 */     this.timeStamp = System.currentTimeMillis();
/* 125 */     String[] split = ParseUtil.whitespaces.split(stats);
/* 126 */     if (split.length < 12)
/*     */     {
/* 128 */       return false;
/*     */     }
/* 130 */     this.bytesSent = ParseUtil.parseUnsignedLongOrDefault(split[10], 0L);
/* 131 */     this.bytesRecv = ParseUtil.parseUnsignedLongOrDefault(split[7], 0L);
/* 132 */     this.packetsSent = ParseUtil.parseUnsignedLongOrDefault(split[8], 0L);
/* 133 */     this.packetsRecv = ParseUtil.parseUnsignedLongOrDefault(split[4], 0L);
/* 134 */     this.outErrors = ParseUtil.parseUnsignedLongOrDefault(split[9], 0L);
/* 135 */     this.inErrors = ParseUtil.parseUnsignedLongOrDefault(split[5], 0L);
/* 136 */     this.collisions = ParseUtil.parseUnsignedLongOrDefault(split[11], 0L);
/* 137 */     this.inDrops = ParseUtil.parseUnsignedLongOrDefault(split[6], 0L);
/* 138 */     return true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platfor\\unix\freebsd\FreeBsdNetworkIF.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */