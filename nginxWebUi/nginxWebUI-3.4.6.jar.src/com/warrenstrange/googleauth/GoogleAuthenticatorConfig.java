/*     */ package com.warrenstrange.googleauth;
/*     */ 
/*     */ import java.util.concurrent.TimeUnit;
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
/*     */ public class GoogleAuthenticatorConfig
/*     */ {
/*  37 */   private long timeStepSizeInMillis = TimeUnit.SECONDS.toMillis(30L);
/*  38 */   private int windowSize = 3;
/*  39 */   private int codeDigits = 6;
/*  40 */   private int numberOfScratchCodes = 5;
/*  41 */   private int keyModulus = (int)Math.pow(10.0D, this.codeDigits);
/*  42 */   private int secretBits = 160;
/*  43 */   private KeyRepresentation keyRepresentation = KeyRepresentation.BASE32;
/*  44 */   private HmacHashFunction hmacHashFunction = HmacHashFunction.HmacSHA1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getKeyModulus() {
/*  53 */     return this.keyModulus;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public KeyRepresentation getKeyRepresentation() {
/*  63 */     return this.keyRepresentation;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getCodeDigits() {
/*  74 */     return this.codeDigits;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getNumberOfScratchCodes() {
/*  84 */     return this.numberOfScratchCodes;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getTimeStepSizeInMillis() {
/*  95 */     return this.timeStepSizeInMillis;
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
/*     */   public int getWindowSize() {
/* 114 */     return this.windowSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getSecretBits() {
/* 125 */     return this.secretBits;
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
/*     */   public HmacHashFunction getHmacHashFunction() {
/* 138 */     return this.hmacHashFunction;
/*     */   }
/*     */   
/*     */   public static class GoogleAuthenticatorConfigBuilder
/*     */   {
/* 143 */     private GoogleAuthenticatorConfig config = new GoogleAuthenticatorConfig();
/*     */ 
/*     */     
/*     */     public GoogleAuthenticatorConfig build() {
/* 147 */       return this.config;
/*     */     }
/*     */ 
/*     */     
/*     */     public GoogleAuthenticatorConfigBuilder setCodeDigits(int codeDigits) {
/* 152 */       if (codeDigits <= 0)
/*     */       {
/* 154 */         throw new IllegalArgumentException("Code digits must be positive.");
/*     */       }
/*     */       
/* 157 */       if (codeDigits < 6)
/*     */       {
/* 159 */         throw new IllegalArgumentException("The minimum number of digits is 6.");
/*     */       }
/*     */       
/* 162 */       if (codeDigits > 8)
/*     */       {
/* 164 */         throw new IllegalArgumentException("The maximum number of digits is 8.");
/*     */       }
/*     */       
/* 167 */       this.config.codeDigits = codeDigits;
/* 168 */       this.config.keyModulus = (int)Math.pow(10.0D, codeDigits);
/* 169 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public GoogleAuthenticatorConfigBuilder setNumberOfScratchCodes(int numberOfScratchCodes) {
/* 174 */       if (numberOfScratchCodes < 0)
/*     */       {
/* 176 */         throw new IllegalArgumentException("The number of scratch codes must not be negative");
/*     */       }
/*     */       
/* 179 */       if (numberOfScratchCodes > 1000)
/*     */       {
/* 181 */         throw new IllegalArgumentException("The maximum number of scratch codes is 1000");
/*     */       }
/*     */       
/* 184 */       this.config.numberOfScratchCodes = numberOfScratchCodes;
/* 185 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public GoogleAuthenticatorConfigBuilder setTimeStepSizeInMillis(long timeStepSizeInMillis) {
/* 190 */       if (timeStepSizeInMillis <= 0L)
/*     */       {
/* 192 */         throw new IllegalArgumentException("Time step size must be positive.");
/*     */       }
/*     */       
/* 195 */       this.config.timeStepSizeInMillis = timeStepSizeInMillis;
/* 196 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public GoogleAuthenticatorConfigBuilder setWindowSize(int windowSize) {
/* 201 */       if (windowSize <= 0)
/*     */       {
/* 203 */         throw new IllegalArgumentException("Window number must be positive.");
/*     */       }
/*     */       
/* 206 */       this.config.windowSize = windowSize;
/* 207 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public GoogleAuthenticatorConfigBuilder setSecretBits(int secretBits) {
/* 212 */       if (secretBits < 128)
/*     */       {
/* 214 */         throw new IllegalArgumentException("Secret bits must be greater than or equal to 128.");
/*     */       }
/*     */       
/* 217 */       if (secretBits % 8 != 0)
/*     */       {
/* 219 */         throw new IllegalArgumentException("Secret bits must be a multiple of 8.");
/*     */       }
/*     */       
/* 222 */       this.config.secretBits = secretBits;
/* 223 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public GoogleAuthenticatorConfigBuilder setKeyRepresentation(KeyRepresentation keyRepresentation) {
/* 228 */       if (keyRepresentation == null)
/*     */       {
/* 230 */         throw new IllegalArgumentException("Key representation cannot be null.");
/*     */       }
/*     */       
/* 233 */       this.config.keyRepresentation = keyRepresentation;
/* 234 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public GoogleAuthenticatorConfigBuilder setHmacHashFunction(HmacHashFunction hmacHashFunction) {
/* 239 */       if (hmacHashFunction == null)
/*     */       {
/* 241 */         throw new IllegalArgumentException("HMAC Hash Function cannot be null.");
/*     */       }
/*     */       
/* 244 */       this.config.hmacHashFunction = hmacHashFunction;
/* 245 */       return this;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\warrenstrange\googleauth\GoogleAuthenticatorConfig.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */