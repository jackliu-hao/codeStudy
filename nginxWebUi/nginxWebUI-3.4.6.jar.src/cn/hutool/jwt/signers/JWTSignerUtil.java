/*     */ package cn.hutool.jwt.signers;
/*     */ 
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import java.security.Key;
/*     */ import java.security.KeyPair;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JWTSignerUtil
/*     */ {
/*     */   public static JWTSigner none() {
/*  24 */     return NoneJWTSigner.NONE;
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
/*     */   public static JWTSigner hs256(byte[] key) {
/*  36 */     return createSigner("HS256", key);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static JWTSigner hs384(byte[] key) {
/*  46 */     return createSigner("HS384", key);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static JWTSigner hs512(byte[] key) {
/*  56 */     return createSigner("HS512", key);
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
/*     */   public static JWTSigner rs256(Key key) {
/*  68 */     return createSigner("RS256", key);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static JWTSigner rs384(Key key) {
/*  78 */     return createSigner("RS384", key);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static JWTSigner rs512(Key key) {
/*  88 */     return createSigner("RS512", key);
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
/*     */   public static JWTSigner es256(Key key) {
/* 100 */     return createSigner("ES256", key);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static JWTSigner es384(Key key) {
/* 110 */     return createSigner("ES384", key);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static JWTSigner es512(Key key) {
/* 120 */     return createSigner("ES512", key);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static JWTSigner createSigner(String algorithmId, byte[] key) {
/* 131 */     Assert.notNull(key, "Signer key must be not null!", new Object[0]);
/*     */     
/* 133 */     if (null == algorithmId || "none".equals(algorithmId)) {
/* 134 */       return none();
/*     */     }
/* 136 */     return new HMacJWTSigner(AlgorithmUtil.getAlgorithm(algorithmId), key);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static JWTSigner createSigner(String algorithmId, KeyPair keyPair) {
/* 147 */     Assert.notNull(keyPair, "Signer key pair must be not null!", new Object[0]);
/*     */     
/* 149 */     if (null == algorithmId || "none".equals(algorithmId)) {
/* 150 */       return none();
/*     */     }
/* 152 */     return new AsymmetricJWTSigner(AlgorithmUtil.getAlgorithm(algorithmId), keyPair);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static JWTSigner createSigner(String algorithmId, Key key) {
/* 163 */     Assert.notNull(key, "Signer key must be not null!", new Object[0]);
/*     */     
/* 165 */     if (null == algorithmId || "none".equals(algorithmId)) {
/* 166 */       return NoneJWTSigner.NONE;
/*     */     }
/* 168 */     if (key instanceof java.security.PrivateKey || key instanceof java.security.PublicKey) {
/* 169 */       return new AsymmetricJWTSigner(AlgorithmUtil.getAlgorithm(algorithmId), key);
/*     */     }
/* 171 */     return new HMacJWTSigner(AlgorithmUtil.getAlgorithm(algorithmId), key);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\jwt\signers\JWTSignerUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */