/*     */ package com.mysql.cj.protocol;
/*     */ 
/*     */ import com.mysql.cj.exceptions.AssertionFailedException;
/*     */ import com.mysql.cj.util.StringUtils;
/*     */ import java.security.DigestException;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Security
/*     */ {
/*  44 */   private static int CACHING_SHA2_DIGEST_LENGTH = 32;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void xorString(byte[] from, byte[] to, byte[] scramble, int length) {
/*  61 */     int pos = 0;
/*  62 */     int scrambleLength = scramble.length;
/*     */     
/*  64 */     while (pos < length) {
/*  65 */       to[pos] = (byte)(from[pos] ^ scramble[pos % scrambleLength]);
/*  66 */       pos++;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public static byte[] scramble411(String password, byte[] seed, String passwordEncoding) {
/*  72 */     byte[] passwordBytes = (passwordEncoding == null || passwordEncoding.length() == 0) ? StringUtils.getBytes(password) : StringUtils.getBytes(password, passwordEncoding);
/*  73 */     return scramble411(passwordBytes, seed);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] scramble411(byte[] password, byte[] seed) {
/*     */     MessageDigest md;
/*     */     try {
/*  99 */       md = MessageDigest.getInstance("SHA-1");
/* 100 */     } catch (NoSuchAlgorithmException ex) {
/* 101 */       throw new AssertionFailedException(ex);
/*     */     } 
/*     */     
/* 104 */     byte[] passwordHashStage1 = md.digest(password);
/* 105 */     md.reset();
/*     */     
/* 107 */     byte[] passwordHashStage2 = md.digest(passwordHashStage1);
/* 108 */     md.reset();
/*     */     
/* 110 */     md.update(seed);
/* 111 */     md.update(passwordHashStage2);
/*     */     
/* 113 */     byte[] toBeXord = md.digest();
/*     */     
/* 115 */     int numToXor = toBeXord.length;
/*     */     
/* 117 */     for (int i = 0; i < numToXor; i++) {
/* 118 */       toBeXord[i] = (byte)(toBeXord[i] ^ passwordHashStage1[i]);
/*     */     }
/*     */     
/* 121 */     return toBeXord;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] scrambleCachingSha2(byte[] password, byte[] seed) throws DigestException {
/*     */     MessageDigest md;
/*     */     try {
/* 151 */       md = MessageDigest.getInstance("SHA-256");
/* 152 */     } catch (NoSuchAlgorithmException ex) {
/* 153 */       throw new AssertionFailedException(ex);
/*     */     } 
/*     */     
/* 156 */     byte[] dig1 = new byte[CACHING_SHA2_DIGEST_LENGTH];
/* 157 */     byte[] dig2 = new byte[CACHING_SHA2_DIGEST_LENGTH];
/* 158 */     byte[] scramble1 = new byte[CACHING_SHA2_DIGEST_LENGTH];
/*     */ 
/*     */     
/* 161 */     md.update(password, 0, password.length);
/* 162 */     md.digest(dig1, 0, CACHING_SHA2_DIGEST_LENGTH);
/* 163 */     md.reset();
/*     */ 
/*     */     
/* 166 */     md.update(dig1, 0, dig1.length);
/* 167 */     md.digest(dig2, 0, CACHING_SHA2_DIGEST_LENGTH);
/* 168 */     md.reset();
/*     */ 
/*     */     
/* 171 */     md.update(dig2, 0, dig1.length);
/* 172 */     md.update(seed, 0, seed.length);
/* 173 */     md.digest(scramble1, 0, CACHING_SHA2_DIGEST_LENGTH);
/*     */ 
/*     */     
/* 176 */     byte[] mysqlScrambleBuff = new byte[CACHING_SHA2_DIGEST_LENGTH];
/* 177 */     xorString(dig1, mysqlScrambleBuff, scramble1, CACHING_SHA2_DIGEST_LENGTH);
/*     */     
/* 179 */     return mysqlScrambleBuff;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\Security.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */