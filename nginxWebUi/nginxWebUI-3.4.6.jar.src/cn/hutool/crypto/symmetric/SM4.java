/*     */ package cn.hutool.crypto.symmetric;
/*     */ 
/*     */ import cn.hutool.core.util.ArrayUtil;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SM4
/*     */   extends SymmetricCrypto
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   public static final String ALGORITHM_NAME = "SM4";
/*     */   
/*     */   public SM4() {
/*  36 */     super("SM4");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SM4(byte[] key) {
/*  45 */     super("SM4", key);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SM4(Mode mode, Padding padding) {
/*  55 */     this(mode.name(), padding.name());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SM4(Mode mode, Padding padding, byte[] key) {
/*  66 */     this(mode, padding, key, (byte[])null);
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
/*     */   public SM4(Mode mode, Padding padding, byte[] key, byte[] iv) {
/*  78 */     this(mode.name(), padding.name(), key, iv);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SM4(Mode mode, Padding padding, SecretKey key) {
/*  89 */     this(mode, padding, key, (IvParameterSpec)null);
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
/*     */   public SM4(Mode mode, Padding padding, SecretKey key, byte[] iv) {
/* 101 */     this(mode, padding, key, ArrayUtil.isEmpty(iv) ? null : new IvParameterSpec(iv));
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
/*     */   public SM4(Mode mode, Padding padding, SecretKey key, IvParameterSpec iv) {
/* 113 */     this(mode.name(), padding.name(), key, iv);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SM4(String mode, String padding) {
/* 123 */     this(mode, padding, (byte[])null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SM4(String mode, String padding, byte[] key) {
/* 134 */     this(mode, padding, key, (byte[])null);
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
/*     */   public SM4(String mode, String padding, byte[] key, byte[] iv) {
/* 146 */     this(mode, padding, 
/* 147 */         SecureUtil.generateKey("SM4", key), 
/* 148 */         ArrayUtil.isEmpty(iv) ? null : new IvParameterSpec(iv));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SM4(String mode, String padding, SecretKey key) {
/* 159 */     this(mode, padding, key, (IvParameterSpec)null);
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
/*     */   public SM4(String mode, String padding, SecretKey key, IvParameterSpec iv) {
/* 171 */     super(StrUtil.format("SM4/{}/{}", new Object[] { mode, padding }), key, iv);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\crypto\symmetric\SM4.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */