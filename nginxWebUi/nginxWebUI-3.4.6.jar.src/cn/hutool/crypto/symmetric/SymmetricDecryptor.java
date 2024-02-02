/*     */ package cn.hutool.crypto.symmetric;
/*     */ 
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.core.util.CharsetUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.crypto.SecureUtil;
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
/*     */ public interface SymmetricDecryptor
/*     */ {
/*     */   byte[] decrypt(byte[] paramArrayOfbyte);
/*     */   
/*     */   void decrypt(InputStream paramInputStream, OutputStream paramOutputStream, boolean paramBoolean);
/*     */   
/*     */   default String decryptStr(byte[] bytes, Charset charset) {
/*  51 */     return StrUtil.str(decrypt(bytes), charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default String decryptStr(byte[] bytes) {
/*  61 */     return decryptStr(bytes, CharsetUtil.CHARSET_UTF_8);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default byte[] decrypt(String data) {
/*  71 */     return decrypt(SecureUtil.decode(data));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default String decryptStr(String data, Charset charset) {
/*  82 */     return StrUtil.str(decrypt(data), charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default String decryptStr(String data) {
/*  92 */     return decryptStr(data, CharsetUtil.CHARSET_UTF_8);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default byte[] decrypt(InputStream data) throws IORuntimeException {
/* 103 */     return decrypt(IoUtil.readBytes(data));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default String decryptStr(InputStream data, Charset charset) {
/* 114 */     return StrUtil.str(decrypt(data), charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default String decryptStr(InputStream data) {
/* 124 */     return decryptStr(data, CharsetUtil.CHARSET_UTF_8);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\crypto\symmetric\SymmetricDecryptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */