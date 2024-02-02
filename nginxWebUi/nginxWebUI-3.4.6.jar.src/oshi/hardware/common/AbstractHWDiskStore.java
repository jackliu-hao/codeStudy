/*    */ package oshi.hardware.common;
/*    */ 
/*    */ import oshi.annotation.concurrent.ThreadSafe;
/*    */ import oshi.hardware.HWDiskStore;
/*    */ import oshi.util.FormatUtil;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @ThreadSafe
/*    */ public abstract class AbstractHWDiskStore
/*    */   implements HWDiskStore
/*    */ {
/*    */   private final String name;
/*    */   private final String model;
/*    */   private final String serial;
/*    */   private final long size;
/*    */   
/*    */   protected AbstractHWDiskStore(String name, String model, String serial, long size) {
/* 39 */     this.name = name;
/* 40 */     this.model = model;
/* 41 */     this.serial = serial;
/* 42 */     this.size = size;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getName() {
/* 47 */     return this.name;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getModel() {
/* 52 */     return this.model;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getSerial() {
/* 57 */     return this.serial;
/*    */   }
/*    */ 
/*    */   
/*    */   public long getSize() {
/* 62 */     return this.size;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 67 */     boolean readwrite = (getReads() > 0L || getWrites() > 0L);
/* 68 */     StringBuilder sb = new StringBuilder();
/* 69 */     sb.append(getName()).append(": ");
/* 70 */     sb.append("(model: ").append(getModel());
/* 71 */     sb.append(" - S/N: ").append(getSerial()).append(") ");
/* 72 */     sb.append("size: ").append((getSize() > 0L) ? FormatUtil.formatBytesDecimal(getSize()) : "?").append(", ");
/* 73 */     sb.append("reads: ").append(readwrite ? Long.valueOf(getReads()) : "?");
/* 74 */     sb.append(" (").append(readwrite ? FormatUtil.formatBytes(getReadBytes()) : "?").append("), ");
/* 75 */     sb.append("writes: ").append(readwrite ? Long.valueOf(getWrites()) : "?");
/* 76 */     sb.append(" (").append(readwrite ? FormatUtil.formatBytes(getWriteBytes()) : "?").append("), ");
/* 77 */     sb.append("xfer: ").append(readwrite ? Long.valueOf(getTransferTime()) : "?");
/* 78 */     return sb.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\common\AbstractHWDiskStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */