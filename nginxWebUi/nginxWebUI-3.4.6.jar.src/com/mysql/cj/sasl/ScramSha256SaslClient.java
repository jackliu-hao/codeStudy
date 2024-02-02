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
/*     */ 
/*     */ public class ScramSha256SaslClient
/*     */   extends ScramShaSaslClient
/*     */ {
/*     */   public static final String IANA_MECHANISM_NAME = "SCRAM-SHA-256";
/*     */   public static final String MECHANISM_NAME = "MYSQLCJ-SCRAM-SHA-256";
/*     */   private static final String SHA256_ALGORITHM = "SHA-256";
/*     */   private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";
/*     */   private static final String PBKCF2_HMAC_SHA256_ALGORITHM = "PBKDF2WithHmacSHA256";
/*     */   private static final int SHA256_HASH_LENGTH = 32;
/*     */   
/*     */   public ScramSha256SaslClient(String authorizationId, String authenticationId, String password) throws SaslException {
/*  65 */     super(authorizationId, authenticationId, password);
/*     */   }
/*     */ 
/*     */   
/*     */   String getIanaMechanismName() {
/*  70 */     return "SCRAM-SHA-256";
/*     */   }
/*     */ 
/*     */   
/*     */   public String getMechanismName() {
/*  75 */     return "MYSQLCJ-SCRAM-SHA-256";
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
/*  90 */       MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
/*  91 */       return sha256.digest(str);
/*  92 */     } catch (NoSuchAlgorithmException e) {
/*  93 */       throw ExceptionFactory.createException("Failed computing authentication hashes", e);
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
/* 112 */       Mac hmacSha256 = Mac.getInstance("HmacSHA256");
/* 113 */       hmacSha256.init(new SecretKeySpec(key, "HmacSHA256"));
/*     */       
/* 115 */       return hmacSha256.doFinal(str);
/* 116 */     } catch (NoSuchAlgorithmException|java.security.InvalidKeyException e) {
/* 117 */       throw ExceptionFactory.createException("Failed computing authentication hashes", e);
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
/* 137 */     KeySpec spec = new PBEKeySpec(str.toCharArray(), salt, iterations, 256);
/*     */     try {
/* 139 */       SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
/* 140 */       return factory.generateSecret(spec).getEncoded();
/* 141 */     } catch (NoSuchAlgorithmException|java.security.spec.InvalidKeySpecException e) {
/* 142 */       throw ExceptionFactory.createException(e.getMessage());
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\sasl\ScramSha256SaslClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */