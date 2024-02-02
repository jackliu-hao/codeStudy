/*     */ package cn.hutool.core.codec;
/*     */ 
/*     */ import cn.hutool.core.exceptions.UtilException;
/*     */ import cn.hutool.core.exceptions.ValidateException;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.util.Arrays;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Base58
/*     */ {
/*     */   private static final int CHECKSUM_SIZE = 4;
/*     */   
/*     */   public static String encodeChecked(Integer version, byte[] data) {
/*  34 */     return encode(addChecksum(version, data));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String encode(byte[] data) {
/*  44 */     return Base58Codec.INSTANCE.encode(data);
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
/*     */   public static byte[] decodeChecked(CharSequence encoded) throws ValidateException {
/*     */     try {
/*  58 */       return decodeChecked(encoded, true);
/*  59 */     } catch (ValidateException ignore) {
/*  60 */       return decodeChecked(encoded, false);
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
/*     */   public static byte[] decodeChecked(CharSequence encoded, boolean withVersion) throws ValidateException {
/*  74 */     byte[] valueWithChecksum = decode(encoded);
/*  75 */     return verifyAndRemoveChecksum(valueWithChecksum, withVersion);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] decode(CharSequence encoded) {
/*  85 */     return Base58Codec.INSTANCE.decode(encoded);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static byte[] verifyAndRemoveChecksum(byte[] data, boolean withVersion) {
/*  96 */     byte[] payload = Arrays.copyOfRange(data, withVersion ? 1 : 0, data.length - 4);
/*  97 */     byte[] checksum = Arrays.copyOfRange(data, data.length - 4, data.length);
/*  98 */     byte[] expectedChecksum = checksum(payload);
/*  99 */     if (false == Arrays.equals(checksum, expectedChecksum)) {
/* 100 */       throw new ValidateException("Base58 checksum is invalid");
/*     */     }
/* 102 */     return payload;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static byte[] addChecksum(Integer version, byte[] payload) {
/*     */     byte[] addressBytes;
/* 114 */     if (null != version) {
/* 115 */       addressBytes = new byte[1 + payload.length + 4];
/* 116 */       addressBytes[0] = (byte)version.intValue();
/* 117 */       System.arraycopy(payload, 0, addressBytes, 1, payload.length);
/*     */     } else {
/* 119 */       addressBytes = new byte[payload.length + 4];
/* 120 */       System.arraycopy(payload, 0, addressBytes, 0, payload.length);
/*     */     } 
/* 122 */     byte[] checksum = checksum(payload);
/* 123 */     System.arraycopy(checksum, 0, addressBytes, addressBytes.length - 4, 4);
/* 124 */     return addressBytes;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static byte[] checksum(byte[] data) {
/* 135 */     byte[] hash = hash256(hash256(data));
/* 136 */     return Arrays.copyOfRange(hash, 0, 4);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static byte[] hash256(byte[] data) {
/*     */     try {
/* 147 */       return MessageDigest.getInstance("SHA-256").digest(data);
/* 148 */     } catch (NoSuchAlgorithmException e) {
/* 149 */       throw new UtilException(e);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\codec\Base58.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */