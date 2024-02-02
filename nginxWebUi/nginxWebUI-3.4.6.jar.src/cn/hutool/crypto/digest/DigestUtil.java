/*     */ package cn.hutool.crypto.digest;
/*     */ 
/*     */ import cn.hutool.core.util.CharsetUtil;
/*     */ import java.io.File;
/*     */ import java.io.InputStream;
/*     */ import java.nio.charset.Charset;
/*     */ import javax.crypto.SecretKey;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DigestUtil
/*     */ {
/*     */   public static byte[] md5(byte[] data) {
/*  26 */     return (new MD5()).digest(data);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] md5(String data, String charset) {
/*  37 */     return (new MD5()).digest(data, charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] md5(String data) {
/*  47 */     return md5(data, "UTF-8");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] md5(InputStream data) {
/*  57 */     return (new MD5()).digest(data);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] md5(File file) {
/*  67 */     return (new MD5()).digest(file);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String md5Hex(byte[] data) {
/*  77 */     return (new MD5()).digestHex(data);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String md5Hex(String data, String charset) {
/*  88 */     return (new MD5()).digestHex(data, charset);
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
/*     */   public static String md5Hex(String data, Charset charset) {
/* 100 */     return (new MD5()).digestHex(data, charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String md5Hex(String data) {
/* 110 */     return md5Hex(data, "UTF-8");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String md5Hex(InputStream data) {
/* 120 */     return (new MD5()).digestHex(data);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String md5Hex(File file) {
/* 130 */     return (new MD5()).digestHex(file);
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
/*     */   public static String md5Hex16(byte[] data) {
/* 143 */     return (new MD5()).digestHex16(data);
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
/*     */   public static String md5Hex16(String data, Charset charset) {
/* 155 */     return (new MD5()).digestHex16(data, charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String md5Hex16(String data) {
/* 166 */     return md5Hex16(data, CharsetUtil.CHARSET_UTF_8);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String md5Hex16(InputStream data) {
/* 177 */     return (new MD5()).digestHex16(data);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String md5Hex16(File file) {
/* 188 */     return (new MD5()).digestHex16(file);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String md5HexTo16(String md5Hex) {
/* 199 */     return md5Hex.substring(8, 24);
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
/*     */   public static byte[] sha1(byte[] data) {
/* 211 */     return (new Digester(DigestAlgorithm.SHA1)).digest(data);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] sha1(String data, String charset) {
/* 222 */     return (new Digester(DigestAlgorithm.SHA1)).digest(data, charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] sha1(String data) {
/* 232 */     return sha1(data, "UTF-8");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] sha1(InputStream data) {
/* 242 */     return (new Digester(DigestAlgorithm.SHA1)).digest(data);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] sha1(File file) {
/* 252 */     return (new Digester(DigestAlgorithm.SHA1)).digest(file);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String sha1Hex(byte[] data) {
/* 262 */     return (new Digester(DigestAlgorithm.SHA1)).digestHex(data);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String sha1Hex(String data, String charset) {
/* 273 */     return (new Digester(DigestAlgorithm.SHA1)).digestHex(data, charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String sha1Hex(String data) {
/* 283 */     return sha1Hex(data, "UTF-8");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String sha1Hex(InputStream data) {
/* 293 */     return (new Digester(DigestAlgorithm.SHA1)).digestHex(data);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String sha1Hex(File file) {
/* 303 */     return (new Digester(DigestAlgorithm.SHA1)).digestHex(file);
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
/*     */   public static byte[] sha256(byte[] data) {
/* 316 */     return (new Digester(DigestAlgorithm.SHA256)).digest(data);
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
/*     */   public static byte[] sha256(String data, String charset) {
/* 328 */     return (new Digester(DigestAlgorithm.SHA256)).digest(data, charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] sha256(String data) {
/* 339 */     return sha256(data, "UTF-8");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] sha256(InputStream data) {
/* 350 */     return (new Digester(DigestAlgorithm.SHA256)).digest(data);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] sha256(File file) {
/* 361 */     return (new Digester(DigestAlgorithm.SHA256)).digest(file);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String sha256Hex(byte[] data) {
/* 372 */     return (new Digester(DigestAlgorithm.SHA256)).digestHex(data);
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
/*     */   public static String sha256Hex(String data, String charset) {
/* 384 */     return (new Digester(DigestAlgorithm.SHA256)).digestHex(data, charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String sha256Hex(String data) {
/* 395 */     return sha256Hex(data, "UTF-8");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String sha256Hex(InputStream data) {
/* 406 */     return (new Digester(DigestAlgorithm.SHA256)).digestHex(data);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String sha256Hex(File file) {
/* 417 */     return (new Digester(DigestAlgorithm.SHA256)).digestHex(file);
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
/*     */   public static HMac hmac(HmacAlgorithm algorithm, byte[] key) {
/* 431 */     return new HMac(algorithm, key);
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
/*     */   public static HMac hmac(HmacAlgorithm algorithm, SecretKey key) {
/* 443 */     return new HMac(algorithm, key);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Digester digester(DigestAlgorithm algorithm) {
/* 454 */     return new Digester(algorithm);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Digester digester(String algorithm) {
/* 465 */     return new Digester(algorithm);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String bcrypt(String password) {
/* 476 */     return BCrypt.hashpw(password);
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
/*     */   public static boolean bcryptCheck(String password, String hashed) {
/* 488 */     return BCrypt.checkpw(password, hashed);
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
/*     */   public static byte[] sha512(byte[] data) {
/* 500 */     return (new Digester(DigestAlgorithm.SHA512)).digest(data);
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
/*     */   public static byte[] sha512(String data, String charset) {
/* 512 */     return (new Digester(DigestAlgorithm.SHA512)).digest(data, charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] sha512(String data) {
/* 522 */     return sha512(data, "UTF-8");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] sha512(InputStream data) {
/* 532 */     return (new Digester(DigestAlgorithm.SHA512)).digest(data);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] sha512(File file) {
/* 542 */     return (new Digester(DigestAlgorithm.SHA512)).digest(file);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String sha512Hex(byte[] data) {
/* 552 */     return (new Digester(DigestAlgorithm.SHA512)).digestHex(data);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String sha512Hex(String data, String charset) {
/* 563 */     return (new Digester(DigestAlgorithm.SHA512)).digestHex(data, charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String sha512Hex(String data) {
/* 573 */     return sha512Hex(data, "UTF-8");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String sha512Hex(InputStream data) {
/* 583 */     return (new Digester(DigestAlgorithm.SHA512)).digestHex(data);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String sha512Hex(File file) {
/* 593 */     return (new Digester(DigestAlgorithm.SHA512)).digestHex(file);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\crypto\digest\DigestUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */