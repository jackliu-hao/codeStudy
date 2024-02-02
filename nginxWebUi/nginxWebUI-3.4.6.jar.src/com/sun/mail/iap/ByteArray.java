/*     */ package com.sun.mail.iap;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
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
/*     */ public class ByteArray
/*     */ {
/*     */   private byte[] bytes;
/*     */   private int start;
/*     */   private int count;
/*     */   
/*     */   public ByteArray(byte[] b, int start, int count) {
/*  61 */     this.bytes = b;
/*  62 */     this.start = start;
/*  63 */     this.count = count;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ByteArray(int size) {
/*  72 */     this(new byte[size], 0, size);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] getBytes() {
/*  80 */     return this.bytes;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] getNewBytes() {
/*  87 */     byte[] b = new byte[this.count];
/*  88 */     System.arraycopy(this.bytes, this.start, b, 0, this.count);
/*  89 */     return b;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getStart() {
/*  96 */     return this.start;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getCount() {
/* 103 */     return this.count;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCount(int count) {
/* 112 */     this.count = count;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ByteArrayInputStream toByteArrayInputStream() {
/* 119 */     return new ByteArrayInputStream(this.bytes, this.start, this.count);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void grow(int incr) {
/* 128 */     byte[] nbuf = new byte[this.bytes.length + incr];
/* 129 */     System.arraycopy(this.bytes, 0, nbuf, 0, this.bytes.length);
/* 130 */     this.bytes = nbuf;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\iap\ByteArray.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */