/*     */ package oshi.hardware.platform.linux;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.net.NetworkInterface;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.function.Function;
/*     */ import java.util.stream.Collectors;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.hardware.NetworkIF;
/*     */ import oshi.hardware.common.AbstractNetworkIF;
/*     */ import oshi.util.FileUtil;
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
/*     */ public final class LinuxNetworkIF
/*     */   extends AbstractNetworkIF
/*     */ {
/*     */   private int ifType;
/*     */   private boolean connectorPresent;
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
/*     */   public LinuxNetworkIF(NetworkInterface netint) {
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
/*  71 */         .map(LinuxNetworkIF::new).collect(Collectors.toList()));
/*     */   }
/*     */ 
/*     */   
/*     */   public int getIfType() {
/*  76 */     return this.ifType;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isConnectorPresent() {
/*  81 */     return this.connectorPresent;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getBytesRecv() {
/*  86 */     return this.bytesRecv;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getBytesSent() {
/*  91 */     return this.bytesSent;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getPacketsRecv() {
/*  96 */     return this.packetsRecv;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getPacketsSent() {
/* 101 */     return this.packetsSent;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getInErrors() {
/* 106 */     return this.inErrors;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getOutErrors() {
/* 111 */     return this.outErrors;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getInDrops() {
/* 116 */     return this.inDrops;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getCollisions() {
/* 121 */     return this.collisions;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getSpeed() {
/* 126 */     return this.speed;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getTimeStamp() {
/* 131 */     return this.timeStamp;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean updateAttributes() {
/*     */     try {
/* 137 */       File ifDir = new File(String.format("/sys/class/net/%s/statistics", new Object[] { getName() }));
/* 138 */       if (!ifDir.isDirectory()) {
/* 139 */         return false;
/*     */       }
/* 141 */     } catch (SecurityException e) {
/* 142 */       return false;
/*     */     } 
/* 144 */     String ifTypePath = String.format("/sys/class/net/%s/type", new Object[] { getName() });
/* 145 */     String carrierPath = String.format("/sys/class/net/%s/carrier", new Object[] { getName() });
/* 146 */     String txBytesPath = String.format("/sys/class/net/%s/statistics/tx_bytes", new Object[] { getName() });
/* 147 */     String rxBytesPath = String.format("/sys/class/net/%s/statistics/rx_bytes", new Object[] { getName() });
/* 148 */     String txPacketsPath = String.format("/sys/class/net/%s/statistics/tx_packets", new Object[] { getName() });
/* 149 */     String rxPacketsPath = String.format("/sys/class/net/%s/statistics/rx_packets", new Object[] { getName() });
/* 150 */     String txErrorsPath = String.format("/sys/class/net/%s/statistics/tx_errors", new Object[] { getName() });
/* 151 */     String rxErrorsPath = String.format("/sys/class/net/%s/statistics/rx_errors", new Object[] { getName() });
/* 152 */     String collisionsPath = String.format("/sys/class/net/%s/statistics/collisions", new Object[] { getName() });
/* 153 */     String rxDropsPath = String.format("/sys/class/net/%s/statistics/rx_dropped", new Object[] { getName() });
/* 154 */     String ifSpeed = String.format("/sys/class/net/%s/speed", new Object[] { getName() });
/*     */     
/* 156 */     this.timeStamp = System.currentTimeMillis();
/* 157 */     this.ifType = FileUtil.getIntFromFile(ifTypePath);
/* 158 */     this.connectorPresent = (FileUtil.getIntFromFile(carrierPath) > 0);
/* 159 */     this.bytesSent = FileUtil.getUnsignedLongFromFile(txBytesPath);
/* 160 */     this.bytesRecv = FileUtil.getUnsignedLongFromFile(rxBytesPath);
/* 161 */     this.packetsSent = FileUtil.getUnsignedLongFromFile(txPacketsPath);
/* 162 */     this.packetsRecv = FileUtil.getUnsignedLongFromFile(rxPacketsPath);
/* 163 */     this.outErrors = FileUtil.getUnsignedLongFromFile(txErrorsPath);
/* 164 */     this.inErrors = FileUtil.getUnsignedLongFromFile(rxErrorsPath);
/* 165 */     this.collisions = FileUtil.getUnsignedLongFromFile(collisionsPath);
/* 166 */     this.inDrops = FileUtil.getUnsignedLongFromFile(rxDropsPath);
/* 167 */     long speedMiB = FileUtil.getUnsignedLongFromFile(ifSpeed);
/*     */     
/* 169 */     this.speed = (speedMiB < 0L) ? 0L : (speedMiB << 20L);
/*     */     
/* 171 */     return true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platform\linux\LinuxNetworkIF.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */