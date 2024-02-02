/*     */ package oshi.hardware.common;
/*     */ 
/*     */ import oshi.annotation.concurrent.Immutable;
/*     */ import oshi.hardware.GraphicsCard;
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
/*     */ @Immutable
/*     */ public abstract class AbstractGraphicsCard
/*     */   implements GraphicsCard
/*     */ {
/*     */   private final String name;
/*     */   private final String deviceId;
/*     */   private final String vendor;
/*     */   private final String versionInfo;
/*     */   private long vram;
/*     */   
/*     */   protected AbstractGraphicsCard(String name, String deviceId, String vendor, String versionInfo, long vram) {
/*  56 */     this.name = name;
/*  57 */     this.deviceId = deviceId;
/*  58 */     this.vendor = vendor;
/*  59 */     this.versionInfo = versionInfo;
/*  60 */     this.vram = vram;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/*  65 */     return this.name;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getDeviceId() {
/*  70 */     return this.deviceId;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getVendor() {
/*  75 */     return this.vendor;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getVersionInfo() {
/*  80 */     return this.versionInfo;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getVRam() {
/*  85 */     return this.vram;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*  90 */     StringBuilder builder = new StringBuilder();
/*  91 */     builder.append("GraphicsCard@");
/*  92 */     builder.append(Integer.toHexString(hashCode()));
/*  93 */     builder.append(" [name=");
/*  94 */     builder.append(this.name);
/*  95 */     builder.append(", deviceId=");
/*  96 */     builder.append(this.deviceId);
/*  97 */     builder.append(", vendor=");
/*  98 */     builder.append(this.vendor);
/*  99 */     builder.append(", vRam=");
/* 100 */     builder.append(this.vram);
/* 101 */     builder.append(", versionInfo=[");
/* 102 */     builder.append(this.versionInfo);
/* 103 */     builder.append("]]");
/* 104 */     return builder.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\common\AbstractGraphicsCard.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */