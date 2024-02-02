/*     */ package com.warrenstrange.googleauth;
/*     */ 
/*     */ import java.util.ArrayList;
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
/*     */ public final class GoogleAuthenticatorKey
/*     */ {
/*     */   private final GoogleAuthenticatorConfig config;
/*     */   private final String key;
/*     */   private final int verificationCode;
/*     */   private final List<Integer> scratchCodes;
/*     */   
/*     */   private GoogleAuthenticatorKey(GoogleAuthenticatorConfig config, String key, int verificationCode, List<Integer> scratchCodes) {
/*  85 */     if (key == null)
/*     */     {
/*  87 */       throw new IllegalArgumentException("Key cannot be null");
/*     */     }
/*     */     
/*  90 */     if (config == null)
/*     */     {
/*  92 */       throw new IllegalArgumentException("Configuration cannot be null");
/*     */     }
/*     */     
/*  95 */     if (scratchCodes == null)
/*     */     {
/*  97 */       throw new IllegalArgumentException("Scratch codes cannot be null");
/*     */     }
/*     */     
/* 100 */     this.config = config;
/* 101 */     this.key = key;
/* 102 */     this.verificationCode = verificationCode;
/* 103 */     this.scratchCodes = new ArrayList<>(scratchCodes);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<Integer> getScratchCodes() {
/* 113 */     return this.scratchCodes;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public GoogleAuthenticatorConfig getConfig() {
/* 123 */     return this.config;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getKey() {
/* 133 */     return this.key;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getVerificationCode() {
/* 143 */     return this.verificationCode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class Builder
/*     */   {
/* 151 */     private GoogleAuthenticatorConfig config = new GoogleAuthenticatorConfig();
/*     */     private String key;
/*     */     private int verificationCode;
/* 154 */     private List<Integer> scratchCodes = new ArrayList<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder(String key) {
/* 164 */       this.key = key;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public GoogleAuthenticatorKey build() {
/* 175 */       return new GoogleAuthenticatorKey(this.config, this.key, this.verificationCode, this.scratchCodes);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder setConfig(GoogleAuthenticatorConfig config) {
/* 187 */       this.config = config;
/* 188 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder setKey(String key) {
/* 200 */       this.key = key;
/* 201 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder setVerificationCode(int verificationCode) {
/* 213 */       this.verificationCode = verificationCode;
/* 214 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder setScratchCodes(List<Integer> scratchCodes) {
/* 226 */       this.scratchCodes = scratchCodes;
/* 227 */       return this;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\warrenstrange\googleauth\GoogleAuthenticatorKey.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */