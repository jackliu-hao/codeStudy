/*     */ package cn.hutool.crypto.asymmetric;
/*     */ 
/*     */ import cn.hutool.core.codec.BCD;
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import cn.hutool.core.util.CharsetUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.crypto.SecureUtil;
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
/*     */ 
/*     */ public interface AsymmetricDecryptor
/*     */ {
/*     */   byte[] decrypt(byte[] paramArrayOfbyte, KeyType paramKeyType);
/*     */   
/*     */   default byte[] decrypt(InputStream data, KeyType keyType) throws IORuntimeException {
/*  46 */     return decrypt(IoUtil.readBytes(data), keyType);
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
/*     */   default byte[] decrypt(String data, KeyType keyType) {
/*  58 */     return decrypt(SecureUtil.decode(data), keyType);
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
/*     */   default String decryptStr(String data, KeyType keyType, Charset charset) {
/*  71 */     return StrUtil.str(decrypt(data, keyType), charset);
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
/*     */   default String decryptStr(String data, KeyType keyType) {
/*  83 */     return decryptStr(data, keyType, CharsetUtil.CHARSET_UTF_8);
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
/*     */   default byte[] decryptFromBcd(String data, KeyType keyType) {
/*  95 */     return decryptFromBcd(data, keyType, CharsetUtil.CHARSET_UTF_8);
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
/*     */   default byte[] decryptFromBcd(String data, KeyType keyType, Charset charset) {
/* 108 */     Assert.notNull(data, "Bcd string must be not null!", new Object[0]);
/* 109 */     byte[] dataBytes = BCD.ascToBcd(StrUtil.bytes(data, charset));
/* 110 */     return decrypt(dataBytes, keyType);
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
/*     */   default String decryptStrFromBcd(String data, KeyType keyType, Charset charset) {
/* 123 */     return StrUtil.str(decryptFromBcd(data, keyType, charset), charset);
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
/*     */   default String decryptStrFromBcd(String data, KeyType keyType) {
/* 135 */     return decryptStrFromBcd(data, keyType, CharsetUtil.CHARSET_UTF_8);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\crypto\asymmetric\AsymmetricDecryptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */