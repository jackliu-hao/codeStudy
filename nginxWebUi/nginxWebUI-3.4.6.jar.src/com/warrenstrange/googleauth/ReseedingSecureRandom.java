/*     */ package com.warrenstrange.googleauth;
/*     */ 
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.NoSuchProviderException;
/*     */ import java.security.SecureRandom;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
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
/*     */ class ReseedingSecureRandom
/*     */ {
/*     */   private static final int MAX_OPERATIONS = 1000000;
/*     */   private final String provider;
/*     */   private final String algorithm;
/*  43 */   private final AtomicInteger count = new AtomicInteger(0);
/*     */   
/*     */   private volatile SecureRandom secureRandom;
/*     */ 
/*     */   
/*     */   ReseedingSecureRandom() {
/*  49 */     this.algorithm = null;
/*  50 */     this.provider = null;
/*     */     
/*  52 */     buildSecureRandom();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   ReseedingSecureRandom(String algorithm) {
/*  58 */     if (algorithm == null)
/*     */     {
/*  60 */       throw new IllegalArgumentException("Algorithm cannot be null.");
/*     */     }
/*     */     
/*  63 */     this.algorithm = algorithm;
/*  64 */     this.provider = null;
/*     */     
/*  66 */     buildSecureRandom();
/*     */   }
/*     */ 
/*     */   
/*     */   ReseedingSecureRandom(String algorithm, String provider) {
/*  71 */     if (algorithm == null)
/*     */     {
/*  73 */       throw new IllegalArgumentException("Algorithm cannot be null.");
/*     */     }
/*     */     
/*  76 */     if (provider == null)
/*     */     {
/*  78 */       throw new IllegalArgumentException("Provider cannot be null.");
/*     */     }
/*     */     
/*  81 */     this.algorithm = algorithm;
/*  82 */     this.provider = provider;
/*     */     
/*  84 */     buildSecureRandom();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void buildSecureRandom() {
/*     */     try {
/*  91 */       if (this.algorithm == null && this.provider == null)
/*     */       {
/*  93 */         this.secureRandom = new SecureRandom();
/*     */       }
/*  95 */       else if (this.provider == null)
/*     */       {
/*  97 */         this.secureRandom = SecureRandom.getInstance(this.algorithm);
/*     */       }
/*     */       else
/*     */       {
/* 101 */         this.secureRandom = SecureRandom.getInstance(this.algorithm, this.provider);
/*     */       }
/*     */     
/* 104 */     } catch (NoSuchAlgorithmException e) {
/*     */       
/* 106 */       throw new GoogleAuthenticatorException(
/* 107 */           String.format("Could not initialise SecureRandom with the specified algorithm: %s. Another provider can be chosen setting the %s system property.", new Object[] { this.algorithm, "com.warrenstrange.googleauth.rng.algorithm" }), e);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     }
/* 115 */     catch (NoSuchProviderException e) {
/*     */       
/* 117 */       throw new GoogleAuthenticatorException(
/* 118 */           String.format("Could not initialise SecureRandom with the specified provider: %s. Another provider can be chosen setting the %s system property.", new Object[] { this.provider, "com.warrenstrange.googleauth.rng.algorithmProvider" }), e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void nextBytes(byte[] bytes) {
/* 130 */     if (this.count.incrementAndGet() > 1000000)
/*     */     {
/* 132 */       synchronized (this) {
/*     */         
/* 134 */         if (this.count.get() > 1000000) {
/*     */           
/* 136 */           buildSecureRandom();
/* 137 */           this.count.set(0);
/*     */         } 
/*     */       } 
/*     */     }
/*     */     
/* 142 */     this.secureRandom.nextBytes(bytes);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\warrenstrange\googleauth\ReseedingSecureRandom.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */