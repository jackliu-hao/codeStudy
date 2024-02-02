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
/*     */ @Immutable
/*     */ public class PhysicalMemory
/*     */ {
/*     */   private final String bankLabel;
/*     */   private final long capacity;
/*     */   private final long clockSpeed;
/*     */   private final String manufacturer;
/*     */   private final String memoryType;
/*     */   
/*     */   public PhysicalMemory(String bankLabel, long capacity, long clockSpeed, String manufacturer, String memoryType) {
/*  43 */     this.bankLabel = bankLabel;
/*  44 */     this.capacity = capacity;
/*  45 */     this.clockSpeed = clockSpeed;
/*  46 */     this.manufacturer = manufacturer;
/*  47 */     this.memoryType = memoryType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getBankLabel() {
/*  56 */     return this.bankLabel;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getCapacity() {
/*  65 */     return this.capacity;
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
/*     */   public long getClockSpeed() {
/*  77 */     return this.clockSpeed;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getManufacturer() {
/*  86 */     return this.manufacturer;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getMemoryType() {
/*  95 */     return this.memoryType;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 100 */     StringBuilder sb = new StringBuilder();
/* 101 */     sb.append("Bank label: " + getBankLabel());
/* 102 */     sb.append(", Capacity: " + FormatUtil.formatBytes(getCapacity()));
/* 103 */     sb.append(", Clock speed: " + FormatUtil.formatHertz(getClockSpeed()));
/* 104 */     sb.append(", Manufacturer: " + getManufacturer());
/* 105 */     sb.append(", Memory type: " + getMemoryType());
/* 106 */     return sb.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\PhysicalMemory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */