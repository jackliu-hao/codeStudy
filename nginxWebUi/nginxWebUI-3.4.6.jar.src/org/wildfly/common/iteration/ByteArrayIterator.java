/*     */ package org.wildfly.common.iteration;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.Signature;
/*     */ import java.security.SignatureException;
/*     */ import java.util.Arrays;
/*     */ import java.util.NoSuchElementException;
/*     */ import javax.crypto.Mac;
/*     */ import org.wildfly.common.bytes.ByteStringBuilder;
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
/*     */ final class ByteArrayIterator
/*     */   extends ByteIterator
/*     */ {
/*     */   private final int len;
/*     */   private final byte[] bytes;
/*     */   private final int offs;
/*     */   private int idx;
/*     */   
/*     */   ByteArrayIterator(int len, byte[] bytes, int offs) {
/*  42 */     this.len = len;
/*  43 */     this.bytes = bytes;
/*  44 */     this.offs = offs;
/*  45 */     this.idx = 0;
/*     */   }
/*     */   
/*     */   public boolean hasNext() {
/*  49 */     return (this.idx < this.len);
/*     */   }
/*     */   
/*     */   public boolean hasPrevious() {
/*  53 */     return (this.idx > 0);
/*     */   }
/*     */   
/*     */   public int next() {
/*  57 */     if (!hasNext()) throw new NoSuchElementException(); 
/*  58 */     return this.bytes[this.offs + this.idx++] & 0xFF;
/*     */   }
/*     */   
/*     */   public int previous() {
/*  62 */     if (!hasPrevious()) throw new NoSuchElementException(); 
/*  63 */     return this.bytes[this.offs + --this.idx] & 0xFF;
/*     */   }
/*     */   
/*     */   public int peekNext() throws NoSuchElementException {
/*  67 */     if (!hasNext()) throw new NoSuchElementException(); 
/*  68 */     return this.bytes[this.offs + this.idx] & 0xFF;
/*     */   }
/*     */   
/*     */   public int peekPrevious() throws NoSuchElementException {
/*  72 */     if (!hasPrevious()) throw new NoSuchElementException(); 
/*  73 */     return this.bytes[this.offs + this.idx - 1] & 0xFF;
/*     */   }
/*     */   
/*     */   public long getIndex() {
/*  77 */     return this.idx;
/*     */   }
/*     */   
/*     */   public void update(MessageDigest digest) throws IllegalStateException {
/*  81 */     digest.update(this.bytes, this.offs + this.idx, this.len - this.idx);
/*  82 */     this.idx = this.len;
/*     */   }
/*     */   
/*     */   public ByteIterator doFinal(MessageDigest digest) throws IllegalStateException {
/*  86 */     update(digest);
/*  87 */     return ByteIterator.ofBytes(digest.digest());
/*     */   }
/*     */   
/*     */   public void update(Mac mac) throws IllegalStateException {
/*  91 */     mac.update(this.bytes, this.offs + this.idx, this.len - this.idx);
/*  92 */     this.idx = this.len;
/*     */   }
/*     */   
/*     */   public ByteIterator doFinal(Mac mac) throws IllegalStateException {
/*  96 */     update(mac);
/*  97 */     return ByteIterator.ofBytes(mac.doFinal());
/*     */   }
/*     */   
/*     */   public void update(Signature signature) throws SignatureException {
/* 101 */     signature.update(this.bytes, this.offs + this.idx, this.len - this.idx);
/* 102 */     this.idx = this.len;
/*     */   }
/*     */   
/*     */   public boolean verify(Signature signature) throws SignatureException {
/*     */     try {
/* 107 */       return signature.verify(this.bytes, this.offs + this.idx, this.len - this.idx);
/*     */     } finally {
/* 109 */       this.idx = this.len;
/*     */     } 
/*     */   }
/*     */   
/*     */   public ByteArrayOutputStream drainTo(ByteArrayOutputStream stream) {
/* 114 */     stream.write(this.bytes, this.offs + this.idx, this.len - this.idx);
/* 115 */     this.idx = this.len;
/* 116 */     return stream;
/*     */   }
/*     */   
/*     */   public byte[] drain() {
/*     */     try {
/* 121 */       return Arrays.copyOfRange(this.bytes, this.offs + this.idx, this.offs + this.len);
/*     */     } finally {
/* 123 */       this.idx = this.len;
/*     */     } 
/*     */   }
/*     */   
/*     */   public int drain(byte[] dst, int offs, int dlen) {
/* 128 */     int cnt = Math.min(this.len - this.idx, dlen);
/* 129 */     System.arraycopy(this.bytes, offs + this.idx, dst, offs, cnt);
/* 130 */     this.idx += cnt;
/* 131 */     return cnt;
/*     */   }
/*     */   
/*     */   public String drainToUtf8(int count) {
/* 135 */     int cnt = Math.min(this.len - this.idx, count);
/* 136 */     String s = new String(this.bytes, this.idx, cnt, StandardCharsets.UTF_8);
/* 137 */     this.idx += cnt;
/* 138 */     return s;
/*     */   }
/*     */   
/*     */   public String drainToLatin1(int count) {
/* 142 */     int cnt = Math.min(this.len - this.idx, count);
/* 143 */     String s = new String(this.bytes, this.idx, cnt, StandardCharsets.ISO_8859_1);
/* 144 */     this.idx += cnt;
/* 145 */     return s;
/*     */   }
/*     */   
/*     */   public ByteStringBuilder appendTo(ByteStringBuilder builder) {
/* 149 */     builder.append(this.bytes, this.offs + this.idx, this.len - this.idx);
/* 150 */     this.idx = this.len;
/* 151 */     return builder;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\iteration\ByteArrayIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */