/*     */ package cn.hutool.crypto.symmetric;
/*     */ 
/*     */ import cn.hutool.core.codec.Base64;
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.core.util.CharsetUtil;
/*     */ import cn.hutool.core.util.HexUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public interface SymmetricEncryptor
/*     */ {
/*     */   byte[] encrypt(byte[] paramArrayOfbyte);
/*     */   
/*     */   void encrypt(InputStream paramInputStream, OutputStream paramOutputStream, boolean paramBoolean);
/*     */   
/*     */   default String encryptHex(byte[] data) {
/*  52 */     return HexUtil.encodeHexStr(encrypt(data));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default String encryptBase64(byte[] data) {
/*  62 */     return Base64.encode(encrypt(data));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default byte[] encrypt(String data, String charset) {
/*  73 */     return encrypt(StrUtil.bytes(data, charset));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default byte[] encrypt(String data, Charset charset) {
/*  84 */     return encrypt(StrUtil.bytes(data, charset));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default String encryptHex(String data, String charset) {
/*  95 */     return HexUtil.encodeHexStr(encrypt(data, charset));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default String encryptHex(String data, Charset charset) {
/* 106 */     return HexUtil.encodeHexStr(encrypt(data, charset));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default String encryptBase64(String data, String charset) {
/* 117 */     return Base64.encode(encrypt(data, charset));
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
/*     */   default String encryptBase64(String data, Charset charset) {
/* 129 */     return Base64.encode(encrypt(data, charset));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default byte[] encrypt(String data) {
/* 139 */     return encrypt(StrUtil.bytes(data, CharsetUtil.CHARSET_UTF_8));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default String encryptHex(String data) {
/* 149 */     return HexUtil.encodeHexStr(encrypt(data));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default String encryptBase64(String data) {
/* 159 */     return Base64.encode(encrypt(data));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default byte[] encrypt(InputStream data) throws IORuntimeException {
/* 170 */     return encrypt(IoUtil.readBytes(data));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default String encryptHex(InputStream data) {
/* 180 */     return HexUtil.encodeHexStr(encrypt(data));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default String encryptBase64(InputStream data) {
/* 190 */     return Base64.encode(encrypt(data));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\crypto\symmetric\SymmetricEncryptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */