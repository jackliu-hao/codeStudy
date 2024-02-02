/*     */ package cn.hutool.crypto.asymmetric;
/*     */ 
/*     */ import cn.hutool.core.codec.BCD;
/*     */ import cn.hutool.core.codec.Base64;
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.core.util.CharsetUtil;
/*     */ import cn.hutool.core.util.HexUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.io.InputStream;
/*     */ import java.nio.charset.Charset;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public interface AsymmetricEncryptor
/*     */ {
/*     */   byte[] encrypt(byte[] paramArrayOfbyte, KeyType paramKeyType);
/*     */   
/*     */   default String encryptHex(byte[] data, KeyType keyType) {
/*  45 */     return HexUtil.encodeHexStr(encrypt(data, keyType));
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
/*     */   default String encryptBase64(byte[] data, KeyType keyType) {
/*  57 */     return Base64.encode(encrypt(data, keyType));
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
/*     */   default byte[] encrypt(String data, String charset, KeyType keyType) {
/*  69 */     return encrypt(StrUtil.bytes(data, charset), keyType);
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
/*     */   default byte[] encrypt(String data, Charset charset, KeyType keyType) {
/*  81 */     return encrypt(StrUtil.bytes(data, charset), keyType);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default byte[] encrypt(String data, KeyType keyType) {
/*  92 */     return encrypt(StrUtil.utf8Bytes(data), keyType);
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
/*     */   default String encryptHex(String data, KeyType keyType) {
/* 104 */     return HexUtil.encodeHexStr(encrypt(data, keyType));
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
/*     */   default String encryptHex(String data, Charset charset, KeyType keyType) {
/* 117 */     return HexUtil.encodeHexStr(encrypt(data, charset, keyType));
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
/*     */   default String encryptBase64(String data, KeyType keyType) {
/* 129 */     return Base64.encode(encrypt(data, keyType));
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
/*     */   default String encryptBase64(String data, Charset charset, KeyType keyType) {
/* 142 */     return Base64.encode(encrypt(data, charset, keyType));
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
/*     */   default byte[] encrypt(InputStream data, KeyType keyType) throws IORuntimeException {
/* 154 */     return encrypt(IoUtil.readBytes(data), keyType);
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
/*     */   default String encryptHex(InputStream data, KeyType keyType) {
/* 166 */     return HexUtil.encodeHexStr(encrypt(data, keyType));
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
/*     */   default String encryptBase64(InputStream data, KeyType keyType) {
/* 178 */     return Base64.encode(encrypt(data, keyType));
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
/*     */   default String encryptBcd(String data, KeyType keyType) {
/* 190 */     return encryptBcd(data, keyType, CharsetUtil.CHARSET_UTF_8);
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
/*     */   default String encryptBcd(String data, KeyType keyType, Charset charset) {
/* 203 */     return BCD.bcdToStr(encrypt(data, charset, keyType));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\crypto\asymmetric\AsymmetricEncryptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */