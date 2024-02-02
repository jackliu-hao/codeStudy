/*     */ package oshi.hardware.platform.unix.aix;
/*     */ 
/*     */ import com.sun.jna.Native;
/*     */ import java.net.NetworkInterface;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.function.Supplier;
/*     */ import java.util.stream.Collectors;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.driver.unix.aix.perfstat.PerfstatNetInterface;
/*     */ import oshi.hardware.NetworkIF;
/*     */ import oshi.hardware.common.AbstractNetworkIF;
/*     */ import oshi.jna.platform.unix.aix.Perfstat;
/*     */ import oshi.util.Memoizer;
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
/*     */ public final class AixNetworkIF
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
/*     */   private long speed;
/*     */   private long timeStamp;
/*     */   private Supplier<Perfstat.perfstat_netinterface_t[]> netstats;
/*     */   
/*     */   public AixNetworkIF(NetworkInterface netint, Supplier<Perfstat.perfstat_netinterface_t[]> netstats) {
/*  63 */     super(netint);
/*  64 */     this.netstats = netstats;
/*  65 */     updateAttributes();
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
/*  77 */     Supplier<Perfstat.perfstat_netinterface_t[]> netstats = Memoizer.memoize(PerfstatNetInterface::queryNetInterfaces, 
/*  78 */         Memoizer.defaultExpiration());
/*  79 */     return Collections.unmodifiableList((List<? extends NetworkIF>)getNetworkInterfaces(includeLocalInterfaces).stream()
/*  80 */         .map(n -> new AixNetworkIF(n, netstats)).collect(Collectors.toList()));
/*     */   }
/*     */ 
/*     */   
/*     */   public long getBytesRecv() {
/*  85 */     return this.bytesRecv;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getBytesSent() {
/*  90 */     return this.bytesSent;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getPacketsRecv() {
/*  95 */     return this.packetsRecv;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getPacketsSent() {
/* 100 */     return this.packetsSent;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getInErrors() {
/* 105 */     return this.inErrors;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getOutErrors() {
/* 110 */     return this.outErrors;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getInDrops() {
/* 115 */     return this.inDrops;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getCollisions() {
/* 120 */     return this.collisions;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getSpeed() {
/* 125 */     return this.speed;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getTimeStamp() {
/* 130 */     return this.timeStamp;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean updateAttributes() {
/* 135 */     Perfstat.perfstat_netinterface_t[] stats = this.netstats.get();
/* 136 */     long now = System.currentTimeMillis();
/* 137 */     for (Perfstat.perfstat_netinterface_t stat : stats) {
/* 138 */       String name = Native.toString(stat.name);
/* 139 */       if (name.equals(getName())) {
/* 140 */         this.bytesSent = stat.obytes;
/* 141 */         this.bytesRecv = stat.ibytes;
/* 142 */         this.packetsSent = stat.opackets;
/* 143 */         this.packetsRecv = stat.ipackets;
/* 144 */         this.outErrors = stat.oerrors;
/* 145 */         this.inErrors = stat.ierrors;
/* 146 */         this.collisions = stat.collisions;
/* 147 */         this.inDrops = stat.if_iqdrops;
/* 148 */         this.speed = stat.bitrate;
/* 149 */         this.timeStamp = now;
/* 150 */         return true;
/*     */       } 
/*     */     } 
/* 153 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platfor\\unix\aix\AixNetworkIF.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */