/*     */ package cn.hutool.crypto.symmetric;
/*     */ 
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.crypto.Mode;
/*     */ import cn.hutool.crypto.Padding;
/*     */ import cn.hutool.crypto.SecureUtil;
/*     */ import javax.crypto.SecretKey;
/*     */ import javax.crypto.spec.IvParameterSpec;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DES
/*     */   extends SymmetricCrypto
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   
/*     */   public DES() {
/*  27 */     super(SymmetricAlgorithm.DES);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DES(byte[] key) {
/*  36 */     super(SymmetricAlgorithm.DES, key);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DES(Mode mode, Padding padding) {
/*  46 */     this(mode.name(), padding.name());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DES(Mode mode, Padding padding, byte[] key) {
/*  57 */     this(mode, padding, key, (byte[])null);
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
/*     */   public DES(Mode mode, Padding padding, byte[] key, byte[] iv) {
/*  70 */     this(mode.name(), padding.name(), key, iv);
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
/*     */   public DES(Mode mode, Padding padding, SecretKey key) {
/*  82 */     this(mode, padding, key, (IvParameterSpec)null);
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
/*     */   public DES(Mode mode, Padding padding, SecretKey key, IvParameterSpec iv) {
/*  95 */     this(mode.name(), padding.name(), key, iv);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DES(String mode, String padding) {
/* 105 */     this(mode, padding, (byte[])null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DES(String mode, String padding, byte[] key) {
/* 116 */     this(mode, padding, SecureUtil.generateKey("DES", key), (IvParameterSpec)null);
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
/*     */   public DES(String mode, String padding, byte[] key, byte[] iv) {
/* 128 */     this(mode, padding, SecureUtil.generateKey("DES", key), (null == iv) ? null : new IvParameterSpec(iv));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DES(String mode, String padding, SecretKey key) {
/* 139 */     this(mode, padding, key, (IvParameterSpec)null);
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
/*     */   public DES(String mode, String padding, SecretKey key, IvParameterSpec iv) {
/* 151 */     super(StrUtil.format("DES/{}/{}", new Object[] { mode, padding }), key, iv);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\crypto\symmetric\DES.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */