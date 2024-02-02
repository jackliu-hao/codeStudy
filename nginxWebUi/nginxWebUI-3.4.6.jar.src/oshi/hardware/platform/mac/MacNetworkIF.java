/*     */ package oshi.hardware.platform.mac;
/*     */ 
/*     */ import java.net.NetworkInterface;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.stream.Collectors;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.driver.mac.net.NetStat;
/*     */ import oshi.hardware.NetworkIF;
/*     */ import oshi.hardware.common.AbstractNetworkIF;
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
/*     */ public final class MacNetworkIF
/*     */   extends AbstractNetworkIF
/*     */ {
/*     */   private int ifType;
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
/*     */   public MacNetworkIF(NetworkInterface netint, Map<Integer, NetStat.IFdata> data) {
/*  57 */     super(netint);
/*  58 */     updateNetworkStats(data);
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
/*     */   
/*     */   public static List<NetworkIF> getNetworks(boolean includeLocalInterfaces) {
/*  71 */     Map<Integer, NetStat.IFdata> data = NetStat.queryIFdata(-1);
/*  72 */     return Collections.unmodifiableList((List<? extends NetworkIF>)getNetworkInterfaces(includeLocalInterfaces).stream()
/*  73 */         .map(ni -> new MacNetworkIF(ni, data)).collect(Collectors.toList()));
/*     */   }
/*     */ 
/*     */   
/*     */   public int getIfType() {
/*  78 */     return this.ifType;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getBytesRecv() {
/*  83 */     return this.bytesRecv;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getBytesSent() {
/*  88 */     return this.bytesSent;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getPacketsRecv() {
/*  93 */     return this.packetsRecv;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getPacketsSent() {
/*  98 */     return this.packetsSent;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getInErrors() {
/* 103 */     return this.inErrors;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getOutErrors() {
/* 108 */     return this.outErrors;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getInDrops() {
/* 113 */     return this.inDrops;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getCollisions() {
/* 118 */     return this.collisions;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getSpeed() {
/* 123 */     return this.speed;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getTimeStamp() {
/* 128 */     return this.timeStamp;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean updateAttributes() {
/* 133 */     int index = queryNetworkInterface().getIndex();
/* 134 */     return updateNetworkStats(NetStat.queryIFdata(index));
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
/*     */   private boolean updateNetworkStats(Map<Integer, NetStat.IFdata> data) {
/* 146 */     int index = queryNetworkInterface().getIndex();
/* 147 */     if (data.containsKey(Integer.valueOf(index))) {
/* 148 */       NetStat.IFdata ifData = data.get(Integer.valueOf(index));
/*     */       
/* 150 */       this.ifType = ifData.getIfType();
/* 151 */       this.bytesSent = ifData.getOBytes();
/* 152 */       this.bytesRecv = ifData.getIBytes();
/* 153 */       this.packetsSent = ifData.getOPackets();
/* 154 */       this.packetsRecv = ifData.getIPackets();
/* 155 */       this.outErrors = ifData.getOErrors();
/* 156 */       this.inErrors = ifData.getIErrors();
/* 157 */       this.collisions = ifData.getCollisions();
/* 158 */       this.inDrops = ifData.getIDrops();
/* 159 */       this.speed = ifData.getSpeed();
/* 160 */       this.timeStamp = ifData.getTimeStamp();
/* 161 */       return true;
/*     */     } 
/* 163 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platform\mac\MacNetworkIF.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */