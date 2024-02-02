/*     */ package com.mysql.cj.sasl;
/*     */ 
/*     */ import com.mysql.cj.exceptions.ExceptionFactory;
/*     */ import java.security.GeneralSecurityException;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.spec.KeySpec;
/*     */ import javax.crypto.Mac;
/*     */ import javax.crypto.SecretKeyFactory;
/*     */ import javax.crypto.spec.PBEKeySpec;
/*     */ import javax.crypto.spec.SecretKeySpec;
/*     */ import javax.security.sasl.SaslException;
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
/*     */ public class ScramSha1SaslClient
/*     */   extends ScramShaSaslClient
/*     */ {
/*     */   public static final String IANA_MECHANISM_NAME = "SCRAM-SHA-1";
/*     */   public static final String MECHANISM_NAME = "MYSQLCJ-SCRAM-SHA-1";
/*     */   private static final String SHA1_ALGORITHM = "SHA-1";
/*     */   private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
/*     */   private static final String PBKCF2_HMAC_SHA1_ALGORITHM = "PBKDF2WithHmacSHA1";
/*     */   private static final int SHA1_HASH_LENGTH = 20;
/*     */   
/*     */   public ScramSha1SaslClient(String authorizationId, String authenticationId, String password) throws SaslException {
/*  64 */     super(authorizationId, authenticationId, password);
/*     */   }
/*     */ 
/*     */   
/*     */   String getIanaMechanismName() {
/*  69 */     return "SCRAM-SHA-1";
/*     */   }
/*     */ 
/*     */   
/*     */   public String getMechanismName() {
/*  74 */     return "MYSQLCJ-SCRAM-SHA-1";
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
/*     */   byte[] h(byte[] str) {
/*     */     try {
/*  89 */       MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
/*  90 */       return sha1.digest(str);
/*  91 */     } catch (NoSuchAlgorithmException e) {
/*  92 */       throw ExceptionFactory.createException("Failed computing authentication hashes", e);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   byte[] hmac(byte[] key, byte[] str) {
/*     */     try {
/* 111 */       Mac hmacSha1 = Mac.getInstance("HmacSHA1");
/* 112 */       hmacSha1.init(new SecretKeySpec(key, "HmacSHA1"));
/*     */       
/* 114 */       return hmacSha1.doFinal(str);
/* 115 */     } catch (NoSuchAlgorithmException|java.security.InvalidKeyException e) {
/* 116 */       throw ExceptionFactory.createException("Failed computing authentication hashes", e);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   byte[] hi(String str, byte[] salt, int iterations) {
/* 136 */     KeySpec spec = new PBEKeySpec(str.toCharArray(), salt, iterations, 160);
/*     */     try {
/* 138 */       SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
/* 139 */       return factory.generateSecret(spec).getEncoded();
/* 140 */     } catch (NoSuchAlgorithmException|java.security.spec.InvalidKeySpecException e) {
/* 141 */       throw ExceptionFactory.createException(e.getMessage());
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\sasl\ScramSha1SaslClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */