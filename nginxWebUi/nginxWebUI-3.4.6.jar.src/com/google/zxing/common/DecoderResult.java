/*     */ package com.google.zxing.common;
/*     */ 
/*     */ import java.util.List;
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
/*     */ public final class DecoderResult
/*     */ {
/*     */   private final byte[] rawBytes;
/*     */   private int numBits;
/*     */   private final String text;
/*     */   private final List<byte[]> byteSegments;
/*     */   private final String ecLevel;
/*     */   private Integer errorsCorrected;
/*     */   private Integer erasures;
/*     */   private Object other;
/*     */   private final int structuredAppendParity;
/*     */   private final int structuredAppendSequenceNumber;
/*     */   
/*     */   public DecoderResult(byte[] rawBytes, String text, List<byte[]> byteSegments, String ecLevel) {
/*  45 */     this(rawBytes, text, byteSegments, ecLevel, -1, -1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DecoderResult(byte[] rawBytes, String text, List<byte[]> byteSegments, String ecLevel, int saSequence, int saParity) {
/*  54 */     this.rawBytes = rawBytes;
/*  55 */     this.numBits = (rawBytes == null) ? 0 : (8 * rawBytes.length);
/*  56 */     this.text = text;
/*  57 */     this.byteSegments = byteSegments;
/*  58 */     this.ecLevel = ecLevel;
/*  59 */     this.structuredAppendParity = saParity;
/*  60 */     this.structuredAppendSequenceNumber = saSequence;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] getRawBytes() {
/*  67 */     return this.rawBytes;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getNumBits() {
/*  75 */     return this.numBits;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setNumBits(int numBits) {
/*  83 */     this.numBits = numBits;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getText() {
/*  90 */     return this.text;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<byte[]> getByteSegments() {
/*  97 */     return this.byteSegments;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getECLevel() {
/* 104 */     return this.ecLevel;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Integer getErrorsCorrected() {
/* 111 */     return this.errorsCorrected;
/*     */   }
/*     */   
/*     */   public void setErrorsCorrected(Integer errorsCorrected) {
/* 115 */     this.errorsCorrected = errorsCorrected;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Integer getErasures() {
/* 122 */     return this.erasures;
/*     */   }
/*     */   
/*     */   public void setErasures(Integer erasures) {
/* 126 */     this.erasures = erasures;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getOther() {
/* 133 */     return this.other;
/*     */   }
/*     */   
/*     */   public void setOther(Object other) {
/* 137 */     this.other = other;
/*     */   }
/*     */   
/*     */   public boolean hasStructuredAppend() {
/* 141 */     return (this.structuredAppendParity >= 0 && this.structuredAppendSequenceNumber >= 0);
/*     */   }
/*     */   
/*     */   public int getStructuredAppendParity() {
/* 145 */     return this.structuredAppendParity;
/*     */   }
/*     */   
/*     */   public int getStructuredAppendSequenceNumber() {
/* 149 */     return this.structuredAppendSequenceNumber;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\common\DecoderResult.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */