/*     */ package oshi.hardware.platform.unix.solaris;
/*     */ 
/*     */ import com.sun.jna.platform.unix.solaris.LibKstat;
/*     */ import java.net.NetworkInterface;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.function.Function;
/*     */ import java.util.stream.Collectors;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.hardware.NetworkIF;
/*     */ import oshi.hardware.common.AbstractNetworkIF;
/*     */ import oshi.util.platform.unix.solaris.KstatUtil;
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
/*     */ public final class SolarisNetworkIF
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
/*     */   
/*     */   public SolarisNetworkIF(NetworkInterface netint) {
/*  57 */     super(netint);
/*  58 */     updateAttributes();
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
/*  70 */     return Collections.unmodifiableList((List<? extends NetworkIF>)getNetworkInterfaces(includeLocalInterfaces).stream()
/*  71 */         .map(SolarisNetworkIF::new).collect(Collectors.toList()));
/*     */   }
/*     */ 
/*     */   
/*     */   public long getBytesRecv() {
/*  76 */     return this.bytesRecv;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getBytesSent() {
/*  81 */     return this.bytesSent;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getPacketsRecv() {
/*  86 */     return this.packetsRecv;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getPacketsSent() {
/*  91 */     return this.packetsSent;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getInErrors() {
/*  96 */     return this.inErrors;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getOutErrors() {
/* 101 */     return this.outErrors;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getInDrops() {
/* 106 */     return this.inDrops;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getCollisions() {
/* 111 */     return this.collisions;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getSpeed() {
/* 116 */     return this.speed;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getTimeStamp() {
/* 121 */     return this.timeStamp;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean updateAttributes() {
/* 126 */     KstatUtil.KstatChain kc = KstatUtil.openChain(); 
/* 127 */     try { LibKstat.Kstat ksp = kc.lookup("link", -1, getName());
/* 128 */       if (ksp == null) {
/* 129 */         ksp = kc.lookup(null, -1, getName());
/*     */       }
/* 131 */       if (ksp != null && kc.read(ksp))
/* 132 */       { this.bytesSent = KstatUtil.dataLookupLong(ksp, "obytes64");
/* 133 */         this.bytesRecv = KstatUtil.dataLookupLong(ksp, "rbytes64");
/* 134 */         this.packetsSent = KstatUtil.dataLookupLong(ksp, "opackets64");
/* 135 */         this.packetsRecv = KstatUtil.dataLookupLong(ksp, "ipackets64");
/* 136 */         this.outErrors = KstatUtil.dataLookupLong(ksp, "oerrors");
/* 137 */         this.inErrors = KstatUtil.dataLookupLong(ksp, "ierrors");
/* 138 */         this.collisions = KstatUtil.dataLookupLong(ksp, "collisions");
/* 139 */         this.inDrops = KstatUtil.dataLookupLong(ksp, "dl_idrops");
/* 140 */         this.speed = KstatUtil.dataLookupLong(ksp, "ifspeed");
/*     */         
/* 142 */         this.timeStamp = ksp.ks_snaptime / 1000000L;
/* 143 */         boolean bool = true;
/*     */         
/* 145 */         if (kc != null) kc.close();  return bool; }  if (kc != null) kc.close();  } catch (Throwable throwable) { if (kc != null)
/* 146 */         try { kc.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }   throw throwable; }  return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platfor\\unix\solaris\SolarisNetworkIF.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */