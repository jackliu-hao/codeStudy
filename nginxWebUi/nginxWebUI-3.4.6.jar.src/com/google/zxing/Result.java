/*     */ package com.google.zxing;
/*     */ 
/*     */ import java.util.EnumMap;
/*     */ import java.util.Map;
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
/*     */ public final class Result
/*     */ {
/*     */   private final String text;
/*     */   private final byte[] rawBytes;
/*     */   private final int numBits;
/*     */   private ResultPoint[] resultPoints;
/*     */   private final BarcodeFormat format;
/*     */   private Map<ResultMetadataType, Object> resultMetadata;
/*     */   private final long timestamp;
/*     */   
/*     */   public Result(String text, byte[] rawBytes, ResultPoint[] resultPoints, BarcodeFormat format) {
/*  41 */     this(text, rawBytes, resultPoints, format, System.currentTimeMillis());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Result(String text, byte[] rawBytes, ResultPoint[] resultPoints, BarcodeFormat format, long timestamp) {
/*  49 */     this(text, rawBytes, (rawBytes == null) ? 0 : (8 * rawBytes.length), resultPoints, format, timestamp);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Result(String text, byte[] rawBytes, int numBits, ResultPoint[] resultPoints, BarcodeFormat format, long timestamp) {
/*  59 */     this.text = text;
/*  60 */     this.rawBytes = rawBytes;
/*  61 */     this.numBits = numBits;
/*  62 */     this.resultPoints = resultPoints;
/*  63 */     this.format = format;
/*  64 */     this.resultMetadata = null;
/*  65 */     this.timestamp = timestamp;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getText() {
/*  72 */     return this.text;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] getRawBytes() {
/*  79 */     return this.rawBytes;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getNumBits() {
/*  87 */     return this.numBits;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ResultPoint[] getResultPoints() {
/*  96 */     return this.resultPoints;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BarcodeFormat getBarcodeFormat() {
/* 103 */     return this.format;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Map<ResultMetadataType, Object> getResultMetadata() {
/* 112 */     return this.resultMetadata;
/*     */   }
/*     */   
/*     */   public void putMetadata(ResultMetadataType type, Object value) {
/* 116 */     if (this.resultMetadata == null) {
/* 117 */       this.resultMetadata = new EnumMap<>(ResultMetadataType.class);
/*     */     }
/* 119 */     this.resultMetadata.put(type, value);
/*     */   }
/*     */   
/*     */   public void putAllMetadata(Map<ResultMetadataType, Object> metadata) {
/* 123 */     if (metadata != null) {
/* 124 */       if (this.resultMetadata == null) {
/* 125 */         this.resultMetadata = metadata; return;
/*     */       } 
/* 127 */       this.resultMetadata.putAll(metadata);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void addResultPoints(ResultPoint[] newPoints) {
/*     */     ResultPoint[] oldPoints;
/* 134 */     if ((oldPoints = this.resultPoints) == null) {
/* 135 */       this.resultPoints = newPoints; return;
/* 136 */     }  if (newPoints != null && newPoints.length > 0) {
/* 137 */       ResultPoint[] allPoints = new ResultPoint[oldPoints.length + newPoints.length];
/* 138 */       System.arraycopy(oldPoints, 0, allPoints, 0, oldPoints.length);
/* 139 */       System.arraycopy(newPoints, 0, allPoints, oldPoints.length, newPoints.length);
/* 140 */       this.resultPoints = allPoints;
/*     */     } 
/*     */   }
/*     */   
/*     */   public long getTimestamp() {
/* 145 */     return this.timestamp;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 150 */     return this.text;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\Result.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */