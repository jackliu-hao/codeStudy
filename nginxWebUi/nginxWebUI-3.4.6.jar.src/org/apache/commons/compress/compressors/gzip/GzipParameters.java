/*     */ package org.apache.commons.compress.compressors.gzip;
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
/*     */ public class GzipParameters
/*     */ {
/*  32 */   private int compressionLevel = -1;
/*     */   private long modificationTime;
/*     */   private String filename;
/*     */   private String comment;
/*  36 */   private int operatingSystem = 255;
/*  37 */   private int bufferSize = 512;
/*     */   
/*     */   public int getCompressionLevel() {
/*  40 */     return this.compressionLevel;
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
/*     */   public void setCompressionLevel(int compressionLevel) {
/*  53 */     if (compressionLevel < -1 || compressionLevel > 9) {
/*  54 */       throw new IllegalArgumentException("Invalid gzip compression level: " + compressionLevel);
/*     */     }
/*  56 */     this.compressionLevel = compressionLevel;
/*     */   }
/*     */   
/*     */   public long getModificationTime() {
/*  60 */     return this.modificationTime;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setModificationTime(long modificationTime) {
/*  69 */     this.modificationTime = modificationTime;
/*     */   }
/*     */   
/*     */   public String getFilename() {
/*  73 */     return this.filename;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setFilename(String fileName) {
/*  82 */     this.filename = fileName;
/*     */   }
/*     */   
/*     */   public String getComment() {
/*  86 */     return this.comment;
/*     */   }
/*     */   
/*     */   public void setComment(String comment) {
/*  90 */     this.comment = comment;
/*     */   }
/*     */   
/*     */   public int getOperatingSystem() {
/*  94 */     return this.operatingSystem;
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
/*     */   public void setOperatingSystem(int operatingSystem) {
/* 121 */     this.operatingSystem = operatingSystem;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getBufferSize() {
/* 131 */     return this.bufferSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setBufferSize(int bufferSize) {
/* 142 */     if (bufferSize <= 0) {
/* 143 */       throw new IllegalArgumentException("invalid buffer size: " + bufferSize);
/*     */     }
/* 145 */     this.bufferSize = bufferSize;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\compressors\gzip\GzipParameters.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */