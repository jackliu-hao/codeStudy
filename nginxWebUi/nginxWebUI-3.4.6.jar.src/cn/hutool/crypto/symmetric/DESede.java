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
/*     */ 
/*     */ public class DESede
/*     */   extends SymmetricCrypto
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   
/*     */   public DESede() {
/*  28 */     super(SymmetricAlgorithm.DESede);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DESede(byte[] key) {
/*  37 */     super(SymmetricAlgorithm.DESede, key);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DESede(Mode mode, Padding padding) {
/*  47 */     this(mode.name(), padding.name());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DESede(Mode mode, Padding padding, byte[] key) {
/*  58 */     this(mode, padding, key, (byte[])null);
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
/*     */   public DESede(Mode mode, Padding padding, byte[] key, byte[] iv) {
/*  71 */     this(mode.name(), padding.name(), key, iv);
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
/*     */   public DESede(Mode mode, Padding padding, SecretKey key) {
/*  83 */     this(mode, padding, key, (IvParameterSpec)null);
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
/*     */   public DESede(Mode mode, Padding padding, SecretKey key, IvParameterSpec iv) {
/*  96 */     this(mode.name(), padding.name(), key, iv);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DESede(String mode, String padding) {
/* 106 */     this(mode, padding, (byte[])null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DESede(String mode, String padding, byte[] key) {
/* 117 */     this(mode, padding, key, (byte[])null);
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
/*     */   public DESede(String mode, String padding, byte[] key, byte[] iv) {
/* 129 */     this(mode, padding, SecureUtil.generateKey(SymmetricAlgorithm.DESede.getValue(), key), (null == iv) ? null : new IvParameterSpec(iv));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DESede(String mode, String padding, SecretKey key) {
/* 140 */     this(mode, padding, key, (IvParameterSpec)null);
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
/*     */   public DESede(String mode, String padding, SecretKey key, IvParameterSpec iv) {
/* 152 */     super(StrUtil.format("{}/{}/{}", new Object[] { SymmetricAlgorithm.DESede.getValue(), mode, padding }), key, iv);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\crypto\symmetric\DESede.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */