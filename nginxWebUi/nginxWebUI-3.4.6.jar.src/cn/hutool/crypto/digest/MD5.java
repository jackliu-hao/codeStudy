/*     */ package cn.hutool.crypto.digest;
/*     */ 
/*     */ import java.io.File;
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
/*     */ public class MD5
/*     */   extends Digester
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   
/*     */   public static MD5 create() {
/*  23 */     return new MD5();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MD5() {
/*  30 */     super(DigestAlgorithm.MD5);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MD5(byte[] salt) {
/*  39 */     this(salt, 0, 1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MD5(byte[] salt, int digestCount) {
/*  49 */     this(salt, 0, digestCount);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MD5(byte[] salt, int saltPosition, int digestCount) {
/*  60 */     this();
/*  61 */     this.salt = salt;
/*  62 */     this.saltPosition = saltPosition;
/*  63 */     this.digestCount = digestCount;
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
/*     */   public String digestHex16(String data, Charset charset) {
/*  75 */     return DigestUtil.md5HexTo16(digestHex(data, charset));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String digestHex16(String data) {
/*  86 */     return DigestUtil.md5HexTo16(digestHex(data));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String digestHex16(InputStream data) {
/*  97 */     return DigestUtil.md5HexTo16(digestHex(data));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String digestHex16(File data) {
/* 107 */     return DigestUtil.md5HexTo16(digestHex(data));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String digestHex16(byte[] data) {
/* 118 */     return DigestUtil.md5HexTo16(digestHex(data));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\crypto\digest\MD5.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */