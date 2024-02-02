/*     */ package org.apache.commons.codec.digest;
/*     */ 
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.SecureRandom;
/*     */ import java.util.Arrays;
/*     */ import java.util.Random;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Md5Crypt
/*     */ {
/*     */   static final String APR1_PREFIX = "$apr1$";
/*     */   private static final int BLOCKSIZE = 16;
/*     */   static final String MD5_PREFIX = "$1$";
/*     */   private static final int ROUNDS = 1000;
/*     */   
/*     */   public static String apr1Crypt(byte[] keyBytes) {
/*  80 */     return apr1Crypt(keyBytes, "$apr1$" + B64.getRandomSalt(8));
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
/*     */   public static String apr1Crypt(byte[] keyBytes, Random random) {
/*  98 */     return apr1Crypt(keyBytes, "$apr1$" + B64.getRandomSalt(8, random));
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
/*     */   public static String apr1Crypt(byte[] keyBytes, String salt) {
/* 121 */     if (salt != null && !salt.startsWith("$apr1$")) {
/* 122 */       salt = "$apr1$" + salt;
/*     */     }
/* 124 */     return md5Crypt(keyBytes, salt, "$apr1$");
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
/*     */   public static String apr1Crypt(String keyBytes) {
/* 142 */     return apr1Crypt(keyBytes.getBytes(StandardCharsets.UTF_8));
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
/*     */   public static String apr1Crypt(String keyBytes, String salt) {
/* 164 */     return apr1Crypt(keyBytes.getBytes(StandardCharsets.UTF_8), salt);
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
/*     */   public static String md5Crypt(byte[] keyBytes) {
/* 184 */     return md5Crypt(keyBytes, "$1$" + B64.getRandomSalt(8));
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
/*     */   public static String md5Crypt(byte[] keyBytes, Random random) {
/* 207 */     return md5Crypt(keyBytes, "$1$" + B64.getRandomSalt(8, random));
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
/*     */   public static String md5Crypt(byte[] keyBytes, String salt) {
/* 230 */     return md5Crypt(keyBytes, salt, "$1$");
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
/*     */   public static String md5Crypt(byte[] keyBytes, String salt, String prefix) {
/* 255 */     return md5Crypt(keyBytes, salt, prefix, new SecureRandom());
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
/*     */   public static String md5Crypt(byte[] keyBytes, String salt, String prefix, Random random) {
/*     */     String saltString;
/* 283 */     int keyLen = keyBytes.length;
/*     */ 
/*     */ 
/*     */     
/* 287 */     if (salt == null) {
/* 288 */       saltString = B64.getRandomSalt(8, random);
/*     */     } else {
/* 290 */       Pattern p = Pattern.compile("^" + prefix.replace("$", "\\$") + "([\\.\\/a-zA-Z0-9]{1,8}).*");
/* 291 */       Matcher m = p.matcher(salt);
/* 292 */       if (!m.find()) {
/* 293 */         throw new IllegalArgumentException("Invalid salt value: " + salt);
/*     */       }
/* 295 */       saltString = m.group(1);
/*     */     } 
/* 297 */     byte[] saltBytes = saltString.getBytes(StandardCharsets.UTF_8);
/*     */     
/* 299 */     MessageDigest ctx = DigestUtils.getMd5Digest();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 304 */     ctx.update(keyBytes);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 309 */     ctx.update(prefix.getBytes(StandardCharsets.UTF_8));
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 314 */     ctx.update(saltBytes);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 319 */     MessageDigest ctx1 = DigestUtils.getMd5Digest();
/* 320 */     ctx1.update(keyBytes);
/* 321 */     ctx1.update(saltBytes);
/* 322 */     ctx1.update(keyBytes);
/* 323 */     byte[] finalb = ctx1.digest();
/* 324 */     int ii = keyLen;
/* 325 */     while (ii > 0) {
/* 326 */       ctx.update(finalb, 0, (ii > 16) ? 16 : ii);
/* 327 */       ii -= 16;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 333 */     Arrays.fill(finalb, (byte)0);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 338 */     ii = keyLen;
/* 339 */     int j = 0;
/* 340 */     while (ii > 0) {
/* 341 */       if ((ii & 0x1) == 1) {
/* 342 */         ctx.update(finalb[0]);
/*     */       } else {
/* 344 */         ctx.update(keyBytes[0]);
/*     */       } 
/* 346 */       ii >>= 1;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 352 */     StringBuilder passwd = new StringBuilder(prefix + saltString + "$");
/* 353 */     finalb = ctx.digest();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 359 */     for (int i = 0; i < 1000; i++) {
/* 360 */       ctx1 = DigestUtils.getMd5Digest();
/* 361 */       if ((i & 0x1) != 0) {
/* 362 */         ctx1.update(keyBytes);
/*     */       } else {
/* 364 */         ctx1.update(finalb, 0, 16);
/*     */       } 
/*     */       
/* 367 */       if (i % 3 != 0) {
/* 368 */         ctx1.update(saltBytes);
/*     */       }
/*     */       
/* 371 */       if (i % 7 != 0) {
/* 372 */         ctx1.update(keyBytes);
/*     */       }
/*     */       
/* 375 */       if ((i & 0x1) != 0) {
/* 376 */         ctx1.update(finalb, 0, 16);
/*     */       } else {
/* 378 */         ctx1.update(keyBytes);
/*     */       } 
/* 380 */       finalb = ctx1.digest();
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 386 */     B64.b64from24bit(finalb[0], finalb[6], finalb[12], 4, passwd);
/* 387 */     B64.b64from24bit(finalb[1], finalb[7], finalb[13], 4, passwd);
/* 388 */     B64.b64from24bit(finalb[2], finalb[8], finalb[14], 4, passwd);
/* 389 */     B64.b64from24bit(finalb[3], finalb[9], finalb[15], 4, passwd);
/* 390 */     B64.b64from24bit(finalb[4], finalb[10], finalb[5], 4, passwd);
/* 391 */     B64.b64from24bit((byte)0, (byte)0, finalb[11], 2, passwd);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 397 */     ctx.reset();
/* 398 */     ctx1.reset();
/* 399 */     Arrays.fill(keyBytes, (byte)0);
/* 400 */     Arrays.fill(saltBytes, (byte)0);
/* 401 */     Arrays.fill(finalb, (byte)0);
/*     */     
/* 403 */     return passwd.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\codec\digest\Md5Crypt.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */