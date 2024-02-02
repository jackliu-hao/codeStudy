/*     */ package cn.hutool.crypto.digest.mac;
/*     */ 
/*     */ import cn.hutool.core.codec.Base64;
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.core.util.CharsetUtil;
/*     */ import cn.hutool.core.util.HexUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.crypto.CryptoException;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.File;
/*     */ import java.io.InputStream;
/*     */ import java.io.Serializable;
/*     */ import java.nio.charset.Charset;
/*     */ import java.security.MessageDigest;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Mac
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private final MacEngine engine;
/*     */   
/*     */   public Mac(MacEngine engine) {
/*  39 */     this.engine = engine;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MacEngine getEngine() {
/*  49 */     return this.engine;
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
/*     */   public byte[] digest(String data, Charset charset) {
/*  62 */     return digest(StrUtil.bytes(data, charset));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] digest(String data) {
/*  72 */     return digest(data, CharsetUtil.CHARSET_UTF_8);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String digestBase64(String data, boolean isUrlSafe) {
/*  83 */     return digestBase64(data, CharsetUtil.CHARSET_UTF_8, isUrlSafe);
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
/*     */   public String digestBase64(String data, Charset charset, boolean isUrlSafe) {
/*  95 */     byte[] digest = digest(data, charset);
/*  96 */     return isUrlSafe ? Base64.encodeUrlSafe(digest) : Base64.encode(digest);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String digestHex(String data, Charset charset) {
/* 107 */     return HexUtil.encodeHexStr(digest(data, charset));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String digestHex(String data) {
/* 117 */     return digestHex(data, CharsetUtil.CHARSET_UTF_8);
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
/*     */   public byte[] digest(File file) throws CryptoException {
/* 129 */     InputStream in = null;
/*     */     try {
/* 131 */       in = FileUtil.getInputStream(file);
/* 132 */       return digest(in);
/*     */     } finally {
/* 134 */       IoUtil.close(in);
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
/*     */   public String digestHex(File file) {
/* 146 */     return HexUtil.encodeHexStr(digest(file));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] digest(byte[] data) {
/* 156 */     return digest(new ByteArrayInputStream(data), -1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String digestHex(byte[] data) {
/* 166 */     return HexUtil.encodeHexStr(digest(data));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] digest(InputStream data) {
/* 176 */     return digest(data, 8192);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String digestHex(InputStream data) {
/* 187 */     return HexUtil.encodeHexStr(digest(data));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] digest(InputStream data, int bufferLength) {
/* 198 */     return this.engine.digest(data, bufferLength);
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
/*     */   public String digestHex(InputStream data, int bufferLength) {
/* 210 */     return HexUtil.encodeHexStr(digest(data, bufferLength));
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
/*     */   public boolean verify(byte[] digest, byte[] digestToCompare) {
/* 224 */     return MessageDigest.isEqual(digest, digestToCompare);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMacLength() {
/* 234 */     return this.engine.getMacLength();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getAlgorithm() {
/* 244 */     return this.engine.getAlgorithm();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\crypto\digest\mac\Mac.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */