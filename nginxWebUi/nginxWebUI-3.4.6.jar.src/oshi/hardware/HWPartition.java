/*     */ package oshi.hardware;
/*     */ 
/*     */ import oshi.annotation.concurrent.Immutable;
/*     */ import oshi.util.FormatUtil;
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
/*     */ @Immutable
/*     */ public class HWPartition
/*     */ {
/*     */   private final String identification;
/*     */   private final String name;
/*     */   private final String type;
/*     */   private final String uuid;
/*     */   private final long size;
/*     */   private final int major;
/*     */   private final int minor;
/*     */   private final String mountPoint;
/*     */   
/*     */   public HWPartition(String identification, String name, String type, String uuid, long size, int major, int minor, String mountPoint) {
/*  69 */     this.identification = identification;
/*  70 */     this.name = name;
/*  71 */     this.type = type;
/*  72 */     this.uuid = uuid;
/*  73 */     this.size = size;
/*  74 */     this.major = major;
/*  75 */     this.minor = minor;
/*  76 */     this.mountPoint = mountPoint;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getIdentification() {
/*  87 */     return this.identification;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getName() {
/*  98 */     return this.name;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getType() {
/* 109 */     return this.type;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getUuid() {
/* 120 */     return this.uuid;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getSize() {
/* 131 */     return this.size;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMajor() {
/* 142 */     return this.major;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMinor() {
/* 153 */     return this.minor;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getMountPoint() {
/* 164 */     return this.mountPoint;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 169 */     StringBuilder sb = new StringBuilder();
/* 170 */     sb.append(getIdentification()).append(": ");
/* 171 */     sb.append(getName()).append(" ");
/* 172 */     sb.append("(").append(getType()).append(") ");
/* 173 */     sb.append("Maj:Min=").append(getMajor()).append(":").append(getMinor()).append(", ");
/* 174 */     sb.append("size: ").append(FormatUtil.formatBytesDecimal(getSize()));
/* 175 */     sb.append(getMountPoint().isEmpty() ? "" : (" @ " + getMountPoint()));
/* 176 */     return sb.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\HWPartition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */