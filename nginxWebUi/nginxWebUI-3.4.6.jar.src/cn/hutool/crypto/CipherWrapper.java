/*     */ package cn.hutool.crypto;
/*     */ 
/*     */ import java.security.InvalidAlgorithmParameterException;
/*     */ import java.security.InvalidKeyException;
/*     */ import java.security.Key;
/*     */ import java.security.SecureRandom;
/*     */ import java.security.spec.AlgorithmParameterSpec;
/*     */ import javax.crypto.Cipher;
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
/*     */ public class CipherWrapper
/*     */ {
/*     */   private final Cipher cipher;
/*     */   private AlgorithmParameterSpec params;
/*     */   private SecureRandom random;
/*     */   
/*     */   public CipherWrapper(String algorithm) {
/*  39 */     this(SecureUtil.createCipher(algorithm));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CipherWrapper(Cipher cipher) {
/*  48 */     this.cipher = cipher;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AlgorithmParameterSpec getParams() {
/*  58 */     return this.params;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CipherWrapper setParams(AlgorithmParameterSpec params) {
/*  68 */     this.params = params;
/*  69 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CipherWrapper setRandom(SecureRandom random) {
/*  79 */     this.random = random;
/*  80 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Cipher getCipher() {
/*  89 */     return this.cipher;
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
/*     */   public CipherWrapper initMode(int mode, Key key) throws InvalidKeyException, InvalidAlgorithmParameterException {
/* 103 */     Cipher cipher = this.cipher;
/* 104 */     AlgorithmParameterSpec params = this.params;
/* 105 */     SecureRandom random = this.random;
/* 106 */     if (null != params) {
/* 107 */       if (null != random) {
/* 108 */         cipher.init(mode, key, params, random);
/*     */       } else {
/* 110 */         cipher.init(mode, key, params);
/*     */       }
/*     */     
/* 113 */     } else if (null != random) {
/* 114 */       cipher.init(mode, key, random);
/*     */     } else {
/* 116 */       cipher.init(mode, key);
/*     */     } 
/*     */     
/* 119 */     return this;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\crypto\CipherWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */