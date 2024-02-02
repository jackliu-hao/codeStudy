/*     */ package org.apache.commons.codec.digest;
/*     */ 
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.security.MessageDigest;
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
/*     */ public class Sha2Crypt
/*     */ {
/*     */   private static final int ROUNDS_DEFAULT = 5000;
/*     */   private static final int ROUNDS_MAX = 999999999;
/*     */   private static final int ROUNDS_MIN = 1000;
/*     */   private static final String ROUNDS_PREFIX = "rounds=";
/*     */   private static final int SHA256_BLOCKSIZE = 32;
/*     */   static final String SHA256_PREFIX = "$5$";
/*     */   private static final int SHA512_BLOCKSIZE = 64;
/*     */   static final String SHA512_PREFIX = "$6$";
/*  70 */   private static final Pattern SALT_PATTERN = Pattern.compile("^\\$([56])\\$(rounds=(\\d+)\\$)?([\\.\\/a-zA-Z0-9]{1,16}).*");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String sha256Crypt(byte[] keyBytes) {
/*  89 */     return sha256Crypt(keyBytes, null);
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
/*     */   public static String sha256Crypt(byte[] keyBytes, String salt) {
/* 110 */     if (salt == null) {
/* 111 */       salt = "$5$" + B64.getRandomSalt(8);
/*     */     }
/* 113 */     return sha2Crypt(keyBytes, salt, "$5$", 32, "SHA-256");
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
/*     */   public static String sha256Crypt(byte[] keyBytes, String salt, Random random) {
/* 136 */     if (salt == null) {
/* 137 */       salt = "$5$" + B64.getRandomSalt(8, random);
/*     */     }
/* 139 */     return sha2Crypt(keyBytes, salt, "$5$", 32, "SHA-256");
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
/*     */ 
/*     */ 
/*     */   
/*     */   private static String sha2Crypt(byte[] keyBytes, String salt, String saltPrefix, int blocksize, String algorithm) {
/* 170 */     int keyLen = keyBytes.length;
/*     */ 
/*     */     
/* 173 */     int rounds = 5000;
/* 174 */     boolean roundsCustom = false;
/* 175 */     if (salt == null) {
/* 176 */       throw new IllegalArgumentException("Salt must not be null");
/*     */     }
/*     */     
/* 179 */     Matcher m = SALT_PATTERN.matcher(salt);
/* 180 */     if (!m.find()) {
/* 181 */       throw new IllegalArgumentException("Invalid salt value: " + salt);
/*     */     }
/* 183 */     if (m.group(3) != null) {
/* 184 */       rounds = Integer.parseInt(m.group(3));
/* 185 */       rounds = Math.max(1000, Math.min(999999999, rounds));
/* 186 */       roundsCustom = true;
/*     */     } 
/* 188 */     String saltString = m.group(4);
/* 189 */     byte[] saltBytes = saltString.getBytes(StandardCharsets.UTF_8);
/* 190 */     int saltLen = saltBytes.length;
/*     */ 
/*     */ 
/*     */     
/* 194 */     MessageDigest ctx = DigestUtils.getDigest(algorithm);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 200 */     ctx.update(keyBytes);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 215 */     ctx.update(saltBytes);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 222 */     MessageDigest altCtx = DigestUtils.getDigest(algorithm);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 228 */     altCtx.update(keyBytes);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 234 */     altCtx.update(saltBytes);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 240 */     altCtx.update(keyBytes);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 246 */     byte[] altResult = altCtx.digest();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 256 */     int cnt = keyBytes.length;
/* 257 */     while (cnt > blocksize) {
/* 258 */       ctx.update(altResult, 0, blocksize);
/* 259 */       cnt -= blocksize;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 264 */     ctx.update(altResult, 0, cnt);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 280 */     cnt = keyBytes.length;
/* 281 */     while (cnt > 0) {
/* 282 */       if ((cnt & 0x1) != 0) {
/* 283 */         ctx.update(altResult, 0, blocksize);
/*     */       } else {
/* 285 */         ctx.update(keyBytes);
/*     */       } 
/* 287 */       cnt >>= 1;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 294 */     altResult = ctx.digest();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 300 */     altCtx = DigestUtils.getDigest(algorithm);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 309 */     for (int i = 1; i <= keyLen; i++) {
/* 310 */       altCtx.update(keyBytes);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 317 */     byte[] tempResult = altCtx.digest();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 329 */     byte[] pBytes = new byte[keyLen];
/* 330 */     int cp = 0;
/* 331 */     while (cp < keyLen - blocksize) {
/* 332 */       System.arraycopy(tempResult, 0, pBytes, cp, blocksize);
/* 333 */       cp += blocksize;
/*     */     } 
/* 335 */     System.arraycopy(tempResult, 0, pBytes, cp, keyLen - cp);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 341 */     altCtx = DigestUtils.getDigest(algorithm);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 350 */     for (int j = 1; j <= 16 + (altResult[0] & 0xFF); j++) {
/* 351 */       altCtx.update(saltBytes);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 358 */     tempResult = altCtx.digest();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 371 */     byte[] sBytes = new byte[saltLen];
/* 372 */     cp = 0;
/* 373 */     while (cp < saltLen - blocksize) {
/* 374 */       System.arraycopy(tempResult, 0, sBytes, cp, blocksize);
/* 375 */       cp += blocksize;
/*     */     } 
/* 377 */     System.arraycopy(tempResult, 0, sBytes, cp, saltLen - cp);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 390 */     for (int k = 0; k <= rounds - 1; k++) {
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 395 */       ctx = DigestUtils.getDigest(algorithm);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 402 */       if ((k & 0x1) != 0) {
/* 403 */         ctx.update(pBytes, 0, keyLen);
/*     */       } else {
/* 405 */         ctx.update(altResult, 0, blocksize);
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 412 */       if (k % 3 != 0) {
/* 413 */         ctx.update(sBytes, 0, saltLen);
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 420 */       if (k % 7 != 0) {
/* 421 */         ctx.update(pBytes, 0, keyLen);
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 429 */       if ((k & 0x1) != 0) {
/* 430 */         ctx.update(altResult, 0, blocksize);
/*     */       } else {
/* 432 */         ctx.update(pBytes, 0, keyLen);
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 439 */       altResult = ctx.digest();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 457 */     StringBuilder buffer = new StringBuilder(saltPrefix);
/* 458 */     if (roundsCustom) {
/* 459 */       buffer.append("rounds=");
/* 460 */       buffer.append(rounds);
/* 461 */       buffer.append("$");
/*     */     } 
/* 463 */     buffer.append(saltString);
/* 464 */     buffer.append("$");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 490 */     if (blocksize == 32) {
/* 491 */       B64.b64from24bit(altResult[0], altResult[10], altResult[20], 4, buffer);
/* 492 */       B64.b64from24bit(altResult[21], altResult[1], altResult[11], 4, buffer);
/* 493 */       B64.b64from24bit(altResult[12], altResult[22], altResult[2], 4, buffer);
/* 494 */       B64.b64from24bit(altResult[3], altResult[13], altResult[23], 4, buffer);
/* 495 */       B64.b64from24bit(altResult[24], altResult[4], altResult[14], 4, buffer);
/* 496 */       B64.b64from24bit(altResult[15], altResult[25], altResult[5], 4, buffer);
/* 497 */       B64.b64from24bit(altResult[6], altResult[16], altResult[26], 4, buffer);
/* 498 */       B64.b64from24bit(altResult[27], altResult[7], altResult[17], 4, buffer);
/* 499 */       B64.b64from24bit(altResult[18], altResult[28], altResult[8], 4, buffer);
/* 500 */       B64.b64from24bit(altResult[9], altResult[19], altResult[29], 4, buffer);
/* 501 */       B64.b64from24bit((byte)0, altResult[31], altResult[30], 3, buffer);
/*     */     } else {
/* 503 */       B64.b64from24bit(altResult[0], altResult[21], altResult[42], 4, buffer);
/* 504 */       B64.b64from24bit(altResult[22], altResult[43], altResult[1], 4, buffer);
/* 505 */       B64.b64from24bit(altResult[44], altResult[2], altResult[23], 4, buffer);
/* 506 */       B64.b64from24bit(altResult[3], altResult[24], altResult[45], 4, buffer);
/* 507 */       B64.b64from24bit(altResult[25], altResult[46], altResult[4], 4, buffer);
/* 508 */       B64.b64from24bit(altResult[47], altResult[5], altResult[26], 4, buffer);
/* 509 */       B64.b64from24bit(altResult[6], altResult[27], altResult[48], 4, buffer);
/* 510 */       B64.b64from24bit(altResult[28], altResult[49], altResult[7], 4, buffer);
/* 511 */       B64.b64from24bit(altResult[50], altResult[8], altResult[29], 4, buffer);
/* 512 */       B64.b64from24bit(altResult[9], altResult[30], altResult[51], 4, buffer);
/* 513 */       B64.b64from24bit(altResult[31], altResult[52], altResult[10], 4, buffer);
/* 514 */       B64.b64from24bit(altResult[53], altResult[11], altResult[32], 4, buffer);
/* 515 */       B64.b64from24bit(altResult[12], altResult[33], altResult[54], 4, buffer);
/* 516 */       B64.b64from24bit(altResult[34], altResult[55], altResult[13], 4, buffer);
/* 517 */       B64.b64from24bit(altResult[56], altResult[14], altResult[35], 4, buffer);
/* 518 */       B64.b64from24bit(altResult[15], altResult[36], altResult[57], 4, buffer);
/* 519 */       B64.b64from24bit(altResult[37], altResult[58], altResult[16], 4, buffer);
/* 520 */       B64.b64from24bit(altResult[59], altResult[17], altResult[38], 4, buffer);
/* 521 */       B64.b64from24bit(altResult[18], altResult[39], altResult[60], 4, buffer);
/* 522 */       B64.b64from24bit(altResult[40], altResult[61], altResult[19], 4, buffer);
/* 523 */       B64.b64from24bit(altResult[62], altResult[20], altResult[41], 4, buffer);
/* 524 */       B64.b64from24bit((byte)0, (byte)0, altResult[63], 2, buffer);
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 532 */     Arrays.fill(tempResult, (byte)0);
/* 533 */     Arrays.fill(pBytes, (byte)0);
/* 534 */     Arrays.fill(sBytes, (byte)0);
/* 535 */     ctx.reset();
/* 536 */     altCtx.reset();
/* 537 */     Arrays.fill(keyBytes, (byte)0);
/* 538 */     Arrays.fill(saltBytes, (byte)0);
/*     */     
/* 540 */     return buffer.toString();
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
/*     */   public static String sha512Crypt(byte[] keyBytes) {
/* 560 */     return sha512Crypt(keyBytes, null);
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
/*     */   public static String sha512Crypt(byte[] keyBytes, String salt) {
/* 582 */     if (salt == null) {
/* 583 */       salt = "$6$" + B64.getRandomSalt(8);
/*     */     }
/* 585 */     return sha2Crypt(keyBytes, salt, "$6$", 64, "SHA-512");
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
/*     */   public static String sha512Crypt(byte[] keyBytes, String salt, Random random) {
/* 612 */     if (salt == null) {
/* 613 */       salt = "$6$" + B64.getRandomSalt(8, random);
/*     */     }
/* 615 */     return sha2Crypt(keyBytes, salt, "$6$", 64, "SHA-512");
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\codec\digest\Sha2Crypt.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */