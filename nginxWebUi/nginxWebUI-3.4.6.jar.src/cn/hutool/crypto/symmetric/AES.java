/*     */ package cn.hutool.crypto.symmetric;
/*     */ 
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.crypto.KeyUtil;
/*     */ import cn.hutool.crypto.Mode;
/*     */ import cn.hutool.crypto.Padding;
/*     */ import java.security.spec.AlgorithmParameterSpec;
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
/*     */ public class AES
/*     */   extends SymmetricCrypto
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   
/*     */   public AES() {
/*  41 */     super(SymmetricAlgorithm.AES);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AES(byte[] key) {
/*  50 */     super(SymmetricAlgorithm.AES, key);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AES(SecretKey key) {
/*  60 */     super(SymmetricAlgorithm.AES, key);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AES(Mode mode, Padding padding) {
/*  70 */     this(mode.name(), padding.name());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AES(Mode mode, Padding padding, byte[] key) {
/*  81 */     this(mode, padding, key, (byte[])null);
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
/*     */   public AES(Mode mode, Padding padding, byte[] key, byte[] iv) {
/*  94 */     this(mode.name(), padding.name(), key, iv);
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
/*     */   public AES(Mode mode, Padding padding, SecretKey key) {
/* 106 */     this(mode, padding, key, (IvParameterSpec)null);
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
/*     */   public AES(Mode mode, Padding padding, SecretKey key, byte[] iv) {
/* 119 */     this(mode, padding, key, ArrayUtil.isEmpty(iv) ? null : new IvParameterSpec(iv));
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
/*     */   public AES(Mode mode, Padding padding, SecretKey key, AlgorithmParameterSpec paramsSpec) {
/* 132 */     this(mode.name(), padding.name(), key, paramsSpec);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AES(String mode, String padding) {
/* 142 */     this(mode, padding, (byte[])null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AES(String mode, String padding, byte[] key) {
/* 153 */     this(mode, padding, key, (byte[])null);
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
/*     */   public AES(String mode, String padding, byte[] key, byte[] iv) {
/* 165 */     this(mode, padding, 
/* 166 */         KeyUtil.generateKey(SymmetricAlgorithm.AES.getValue(), key), 
/* 167 */         ArrayUtil.isEmpty(iv) ? null : new IvParameterSpec(iv));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AES(String mode, String padding, SecretKey key) {
/* 178 */     this(mode, padding, key, (AlgorithmParameterSpec)null);
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
/*     */   public AES(String mode, String padding, SecretKey key, AlgorithmParameterSpec paramsSpec) {
/* 190 */     super(StrUtil.format("AES/{}/{}", new Object[] { mode, padding }), key, paramsSpec);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\crypto\symmetric\AES.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */