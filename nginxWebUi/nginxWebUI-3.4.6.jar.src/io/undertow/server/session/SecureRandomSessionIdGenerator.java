/*     */ package io.undertow.server.session;
/*     */ 
/*     */ import java.security.SecureRandom;
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
/*     */ public class SecureRandomSessionIdGenerator
/*     */   implements SessionIdGenerator
/*     */ {
/*  35 */   private final SecureRandom random = new SecureRandom();
/*     */   
/*  37 */   private volatile int length = 30;
/*     */   
/*     */   private static final char[] SESSION_ID_ALPHABET;
/*     */   
/*     */   private static final String ALPHABET_PROPERTY = "io.undertow.server.session.SecureRandomSessionIdGenerator.ALPHABET";
/*     */   
/*     */   static {
/*  44 */     String alphabet = System.getProperty("io.undertow.server.session.SecureRandomSessionIdGenerator.ALPHABET", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_");
/*  45 */     if (alphabet.length() != 64) {
/*  46 */       throw new RuntimeException("io.undertow.server.session.SecureRandomSessionIdGenerator must be exactly 64 characters long");
/*     */     }
/*  48 */     SESSION_ID_ALPHABET = alphabet.toCharArray();
/*     */   }
/*     */ 
/*     */   
/*     */   public String createSessionId() {
/*  53 */     byte[] bytes = new byte[this.length];
/*  54 */     this.random.nextBytes(bytes);
/*  55 */     return new String(encode(bytes));
/*     */   }
/*     */ 
/*     */   
/*     */   public int getLength() {
/*  60 */     return this.length;
/*     */   }
/*     */   
/*     */   public void setLength(int length) {
/*  64 */     this.length = length;
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
/*     */   private char[] encode(byte[] data) {
/*  76 */     char[] out = new char[(data.length + 2) / 3 * 4];
/*  77 */     char[] alphabet = SESSION_ID_ALPHABET;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  82 */     for (int i = 0, index = 0; i < data.length; i += 3, index += 4) {
/*  83 */       boolean quad = false;
/*  84 */       boolean trip = false;
/*     */       
/*  86 */       int val = 0xFF & data[i];
/*  87 */       val <<= 8;
/*  88 */       if (i + 1 < data.length) {
/*  89 */         val |= 0xFF & data[i + 1];
/*  90 */         trip = true;
/*     */       } 
/*  92 */       val <<= 8;
/*  93 */       if (i + 2 < data.length) {
/*  94 */         val |= 0xFF & data[i + 2];
/*  95 */         quad = true;
/*     */       } 
/*  97 */       out[index + 3] = alphabet[quad ? (val & 0x3F) : 63];
/*  98 */       val >>= 6;
/*  99 */       out[index + 2] = alphabet[trip ? (val & 0x3F) : 63];
/* 100 */       val >>= 6;
/* 101 */       out[index + 1] = alphabet[val & 0x3F];
/* 102 */       val >>= 6;
/* 103 */       out[index] = alphabet[val & 0x3F];
/*     */     } 
/* 105 */     return out;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\session\SecureRandomSessionIdGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */