/*      */ package org.apache.commons.codec.digest;
/*      */ 
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.File;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.security.InvalidKeyException;
/*      */ import java.security.NoSuchAlgorithmException;
/*      */ import javax.crypto.Mac;
/*      */ import javax.crypto.spec.SecretKeySpec;
/*      */ import org.apache.commons.codec.binary.Hex;
/*      */ import org.apache.commons.codec.binary.StringUtils;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public final class HmacUtils
/*      */ {
/*      */   private static final int STREAM_BUFFER_LENGTH = 1024;
/*      */   private final Mac mac;
/*      */   
/*      */   public static boolean isAvailable(String name) {
/*      */     try {
/*   69 */       Mac.getInstance(name);
/*   70 */       return true;
/*   71 */     } catch (NoSuchAlgorithmException e) {
/*   72 */       return false;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isAvailable(HmacAlgorithms name) {
/*      */     try {
/*   85 */       Mac.getInstance(name.getName());
/*   86 */       return true;
/*   87 */     } catch (NoSuchAlgorithmException e) {
/*   88 */       return false;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static Mac getHmacMd5(byte[] key) {
/*  109 */     return getInitializedMac(HmacAlgorithms.HMAC_MD5, key);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static Mac getHmacSha1(byte[] key) {
/*  129 */     return getInitializedMac(HmacAlgorithms.HMAC_SHA_1, key);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static Mac getHmacSha256(byte[] key) {
/*  149 */     return getInitializedMac(HmacAlgorithms.HMAC_SHA_256, key);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static Mac getHmacSha384(byte[] key) {
/*  169 */     return getInitializedMac(HmacAlgorithms.HMAC_SHA_384, key);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static Mac getHmacSha512(byte[] key) {
/*  189 */     return getInitializedMac(HmacAlgorithms.HMAC_SHA_512, key);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Mac getInitializedMac(HmacAlgorithms algorithm, byte[] key) {
/*  209 */     return getInitializedMac(algorithm.getName(), key);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Mac getInitializedMac(String algorithm, byte[] key) {
/*  230 */     if (key == null) {
/*  231 */       throw new IllegalArgumentException("Null key");
/*      */     }
/*      */     
/*      */     try {
/*  235 */       SecretKeySpec keySpec = new SecretKeySpec(key, algorithm);
/*  236 */       Mac mac = Mac.getInstance(algorithm);
/*  237 */       mac.init(keySpec);
/*  238 */       return mac;
/*  239 */     } catch (NoSuchAlgorithmException e) {
/*  240 */       throw new IllegalArgumentException(e);
/*  241 */     } catch (InvalidKeyException e) {
/*  242 */       throw new IllegalArgumentException(e);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static byte[] hmacMd5(byte[] key, byte[] valueToDigest) {
/*  262 */     return (new HmacUtils(HmacAlgorithms.HMAC_MD5, key)).hmac(valueToDigest);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static byte[] hmacMd5(byte[] key, InputStream valueToDigest) throws IOException {
/*  284 */     return (new HmacUtils(HmacAlgorithms.HMAC_MD5, key)).hmac(valueToDigest);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static byte[] hmacMd5(String key, String valueToDigest) {
/*  301 */     return (new HmacUtils(HmacAlgorithms.HMAC_MD5, key)).hmac(valueToDigest);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static String hmacMd5Hex(byte[] key, byte[] valueToDigest) {
/*  318 */     return (new HmacUtils(HmacAlgorithms.HMAC_MD5, key)).hmacHex(valueToDigest);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static String hmacMd5Hex(byte[] key, InputStream valueToDigest) throws IOException {
/*  340 */     return (new HmacUtils(HmacAlgorithms.HMAC_MD5, key)).hmacHex(valueToDigest);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static String hmacMd5Hex(String key, String valueToDigest) {
/*  357 */     return (new HmacUtils(HmacAlgorithms.HMAC_MD5, key)).hmacHex(valueToDigest);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static byte[] hmacSha1(byte[] key, byte[] valueToDigest) {
/*  376 */     return (new HmacUtils(HmacAlgorithms.HMAC_SHA_1, key)).hmac(valueToDigest);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static byte[] hmacSha1(byte[] key, InputStream valueToDigest) throws IOException {
/*  398 */     return (new HmacUtils(HmacAlgorithms.HMAC_SHA_1, key)).hmac(valueToDigest);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static byte[] hmacSha1(String key, String valueToDigest) {
/*  415 */     return (new HmacUtils(HmacAlgorithms.HMAC_SHA_1, key)).hmac(valueToDigest);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static String hmacSha1Hex(byte[] key, byte[] valueToDigest) {
/*  432 */     return (new HmacUtils(HmacAlgorithms.HMAC_SHA_1, key)).hmacHex(valueToDigest);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static String hmacSha1Hex(byte[] key, InputStream valueToDigest) throws IOException {
/*  454 */     return (new HmacUtils(HmacAlgorithms.HMAC_SHA_1, key)).hmacHex(valueToDigest);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static String hmacSha1Hex(String key, String valueToDigest) {
/*  471 */     return (new HmacUtils(HmacAlgorithms.HMAC_SHA_1, key)).hmacHex(valueToDigest);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static byte[] hmacSha256(byte[] key, byte[] valueToDigest) {
/*  490 */     return (new HmacUtils(HmacAlgorithms.HMAC_SHA_256, key)).hmac(valueToDigest);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static byte[] hmacSha256(byte[] key, InputStream valueToDigest) throws IOException {
/*  512 */     return (new HmacUtils(HmacAlgorithms.HMAC_SHA_256, key)).hmac(valueToDigest);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static byte[] hmacSha256(String key, String valueToDigest) {
/*  529 */     return (new HmacUtils(HmacAlgorithms.HMAC_SHA_256, key)).hmac(valueToDigest);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static String hmacSha256Hex(byte[] key, byte[] valueToDigest) {
/*  546 */     return (new HmacUtils(HmacAlgorithms.HMAC_SHA_256, key)).hmacHex(valueToDigest);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static String hmacSha256Hex(byte[] key, InputStream valueToDigest) throws IOException {
/*  568 */     return (new HmacUtils(HmacAlgorithms.HMAC_SHA_256, key)).hmacHex(valueToDigest);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static String hmacSha256Hex(String key, String valueToDigest) {
/*  585 */     return (new HmacUtils(HmacAlgorithms.HMAC_SHA_256, key)).hmacHex(valueToDigest);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static byte[] hmacSha384(byte[] key, byte[] valueToDigest) {
/*  604 */     return (new HmacUtils(HmacAlgorithms.HMAC_SHA_384, key)).hmac(valueToDigest);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static byte[] hmacSha384(byte[] key, InputStream valueToDigest) throws IOException {
/*  626 */     return (new HmacUtils(HmacAlgorithms.HMAC_SHA_384, key)).hmac(valueToDigest);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static byte[] hmacSha384(String key, String valueToDigest) {
/*  643 */     return (new HmacUtils(HmacAlgorithms.HMAC_SHA_384, key)).hmac(valueToDigest);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static String hmacSha384Hex(byte[] key, byte[] valueToDigest) {
/*  660 */     return (new HmacUtils(HmacAlgorithms.HMAC_SHA_384, key)).hmacHex(valueToDigest);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static String hmacSha384Hex(byte[] key, InputStream valueToDigest) throws IOException {
/*  682 */     return (new HmacUtils(HmacAlgorithms.HMAC_SHA_384, key)).hmacHex(valueToDigest);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static String hmacSha384Hex(String key, String valueToDigest) {
/*  699 */     return (new HmacUtils(HmacAlgorithms.HMAC_SHA_384, key)).hmacHex(valueToDigest);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static byte[] hmacSha512(byte[] key, byte[] valueToDigest) {
/*  718 */     return (new HmacUtils(HmacAlgorithms.HMAC_SHA_512, key)).hmac(valueToDigest);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static byte[] hmacSha512(byte[] key, InputStream valueToDigest) throws IOException {
/*  740 */     return (new HmacUtils(HmacAlgorithms.HMAC_SHA_512, key)).hmac(valueToDigest);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static byte[] hmacSha512(String key, String valueToDigest) {
/*  757 */     return (new HmacUtils(HmacAlgorithms.HMAC_SHA_512, key)).hmac(valueToDigest);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static String hmacSha512Hex(byte[] key, byte[] valueToDigest) {
/*  774 */     return (new HmacUtils(HmacAlgorithms.HMAC_SHA_512, key)).hmacHex(valueToDigest);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static String hmacSha512Hex(byte[] key, InputStream valueToDigest) throws IOException {
/*  796 */     return (new HmacUtils(HmacAlgorithms.HMAC_SHA_512, key)).hmacHex(valueToDigest);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static String hmacSha512Hex(String key, String valueToDigest) {
/*  813 */     return (new HmacUtils(HmacAlgorithms.HMAC_SHA_512, key)).hmacHex(valueToDigest);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Mac updateHmac(Mac mac, byte[] valueToDigest) {
/*  830 */     mac.reset();
/*  831 */     mac.update(valueToDigest);
/*  832 */     return mac;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Mac updateHmac(Mac mac, InputStream valueToDigest) throws IOException {
/*  852 */     mac.reset();
/*  853 */     byte[] buffer = new byte[1024];
/*  854 */     int read = valueToDigest.read(buffer, 0, 1024);
/*      */     
/*  856 */     while (read > -1) {
/*  857 */       mac.update(buffer, 0, read);
/*  858 */       read = valueToDigest.read(buffer, 0, 1024);
/*      */     } 
/*      */     
/*  861 */     return mac;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Mac updateHmac(Mac mac, String valueToDigest) {
/*  876 */     mac.reset();
/*  877 */     mac.update(StringUtils.getBytesUtf8(valueToDigest));
/*  878 */     return mac;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public HmacUtils() {
/*  888 */     this(null);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private HmacUtils(Mac mac) {
/*  894 */     this.mac = mac;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HmacUtils(String algorithm, byte[] key) {
/*  907 */     this(getInitializedMac(algorithm, key));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HmacUtils(String algorithm, String key) {
/*  920 */     this(algorithm, StringUtils.getBytesUtf8(key));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HmacUtils(HmacAlgorithms algorithm, String key) {
/*  933 */     this(algorithm.getName(), StringUtils.getBytesUtf8(key));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HmacUtils(HmacAlgorithms algorithm, byte[] key) {
/*  946 */     this(algorithm.getName(), key);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public byte[] hmac(byte[] valueToDigest) {
/*  957 */     return this.mac.doFinal(valueToDigest);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String hmacHex(byte[] valueToDigest) {
/*  968 */     return Hex.encodeHexString(hmac(valueToDigest));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public byte[] hmac(String valueToDigest) {
/*  979 */     return this.mac.doFinal(StringUtils.getBytesUtf8(valueToDigest));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String hmacHex(String valueToDigest) {
/*  990 */     return Hex.encodeHexString(hmac(valueToDigest));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public byte[] hmac(ByteBuffer valueToDigest) {
/* 1001 */     this.mac.update(valueToDigest);
/* 1002 */     return this.mac.doFinal();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String hmacHex(ByteBuffer valueToDigest) {
/* 1013 */     return Hex.encodeHexString(hmac(valueToDigest));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public byte[] hmac(InputStream valueToDigest) throws IOException {
/* 1030 */     byte[] buffer = new byte[1024];
/*      */     
/*      */     int read;
/* 1033 */     while ((read = valueToDigest.read(buffer, 0, 1024)) > -1) {
/* 1034 */       this.mac.update(buffer, 0, read);
/*      */     }
/* 1036 */     return this.mac.doFinal();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String hmacHex(InputStream valueToDigest) throws IOException {
/* 1053 */     return Hex.encodeHexString(hmac(valueToDigest));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public byte[] hmac(File valueToDigest) throws IOException {
/* 1066 */     try (BufferedInputStream stream = new BufferedInputStream(new FileInputStream(valueToDigest))) {
/* 1067 */       return hmac(stream);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String hmacHex(File valueToDigest) throws IOException {
/* 1081 */     return Hex.encodeHexString(hmac(valueToDigest));
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\codec\digest\HmacUtils.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */