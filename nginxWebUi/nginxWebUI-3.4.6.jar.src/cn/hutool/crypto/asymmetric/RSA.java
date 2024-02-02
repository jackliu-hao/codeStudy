/*     */ package cn.hutool.crypto.asymmetric;
/*     */ 
/*     */ import cn.hutool.crypto.CryptoException;
/*     */ import cn.hutool.crypto.GlobalBouncyCastleProvider;
/*     */ import cn.hutool.crypto.SecureUtil;
/*     */ import java.math.BigInteger;
/*     */ import java.security.PrivateKey;
/*     */ import java.security.PublicKey;
/*     */ import java.security.interfaces.RSAKey;
/*     */ import java.security.spec.RSAPrivateKeySpec;
/*     */ import java.security.spec.RSAPublicKeySpec;
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
/*     */ public class RSA
/*     */   extends AsymmetricCrypto
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  34 */   private static final AsymmetricAlgorithm ALGORITHM_RSA = AsymmetricAlgorithm.RSA_ECB_PKCS1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static PrivateKey generatePrivateKey(BigInteger modulus, BigInteger privateExponent) {
/*  45 */     return SecureUtil.generatePrivateKey(ALGORITHM_RSA.getValue(), new RSAPrivateKeySpec(modulus, privateExponent));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static PublicKey generatePublicKey(BigInteger modulus, BigInteger publicExponent) {
/*  56 */     return SecureUtil.generatePublicKey(ALGORITHM_RSA.getValue(), new RSAPublicKeySpec(modulus, publicExponent));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RSA() {
/*  65 */     super(ALGORITHM_RSA);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RSA(String rsaAlgorithm) {
/*  74 */     super(rsaAlgorithm);
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
/*     */   public RSA(String privateKeyStr, String publicKeyStr) {
/*  86 */     super(ALGORITHM_RSA, privateKeyStr, publicKeyStr);
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
/*     */   public RSA(String rsaAlgorithm, String privateKeyStr, String publicKeyStr) {
/* 100 */     super(rsaAlgorithm, privateKeyStr, publicKeyStr);
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
/*     */   public RSA(byte[] privateKey, byte[] publicKey) {
/* 112 */     super(ALGORITHM_RSA, privateKey, publicKey);
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
/*     */   public RSA(BigInteger modulus, BigInteger privateExponent, BigInteger publicExponent) {
/* 126 */     this(generatePrivateKey(modulus, privateExponent), generatePublicKey(modulus, publicExponent));
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
/*     */   public RSA(PrivateKey privateKey, PublicKey publicKey) {
/* 139 */     super(ALGORITHM_RSA, privateKey, publicKey);
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
/*     */   public RSA(String rsaAlgorithm, PrivateKey privateKey, PublicKey publicKey) {
/* 153 */     super(rsaAlgorithm, privateKey, publicKey);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] encrypt(byte[] data, KeyType keyType) {
/* 160 */     if (this.encryptBlockSize < 0 && null == GlobalBouncyCastleProvider.INSTANCE.getProvider())
/*     */     {
/* 162 */       this.encryptBlockSize = ((RSAKey)getKeyByType(keyType)).getModulus().bitLength() / 8 - 11;
/*     */     }
/* 164 */     return super.encrypt(data, keyType);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] decrypt(byte[] bytes, KeyType keyType) {
/* 170 */     if (this.decryptBlockSize < 0 && null == GlobalBouncyCastleProvider.INSTANCE.getProvider())
/*     */     {
/* 172 */       this.decryptBlockSize = ((RSAKey)getKeyByType(keyType)).getModulus().bitLength() / 8;
/*     */     }
/* 174 */     return super.decrypt(bytes, keyType);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void initCipher() {
/*     */     try {
/* 180 */       super.initCipher();
/* 181 */     } catch (CryptoException e) {
/* 182 */       Throwable cause = e.getCause();
/* 183 */       if (cause instanceof java.security.NoSuchAlgorithmException) {
/*     */         
/* 185 */         this.algorithm = AsymmetricAlgorithm.RSA.getValue();
/* 186 */         super.initCipher();
/*     */       } 
/* 188 */       throw e;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\crypto\asymmetric\RSA.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */