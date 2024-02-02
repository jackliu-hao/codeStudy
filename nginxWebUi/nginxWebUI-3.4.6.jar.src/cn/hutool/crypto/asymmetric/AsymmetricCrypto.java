/*     */ package cn.hutool.crypto.asymmetric;
/*     */ 
/*     */ import cn.hutool.core.codec.Base64;
/*     */ import cn.hutool.core.io.FastByteArrayOutputStream;
/*     */ import cn.hutool.crypto.CipherWrapper;
/*     */ import cn.hutool.crypto.CryptoException;
/*     */ import cn.hutool.crypto.KeyUtil;
/*     */ import cn.hutool.crypto.SecureUtil;
/*     */ import java.io.IOException;
/*     */ import java.security.InvalidAlgorithmParameterException;
/*     */ import java.security.InvalidKeyException;
/*     */ import java.security.Key;
/*     */ import java.security.PrivateKey;
/*     */ import java.security.PublicKey;
/*     */ import java.security.SecureRandom;
/*     */ import java.security.spec.AlgorithmParameterSpec;
/*     */ import javax.crypto.BadPaddingException;
/*     */ import javax.crypto.Cipher;
/*     */ import javax.crypto.IllegalBlockSizeException;
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
/*     */ public class AsymmetricCrypto
/*     */   extends AbstractAsymmetricCrypto<AsymmetricCrypto>
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected CipherWrapper cipherWrapper;
/*  47 */   protected int encryptBlockSize = -1;
/*     */ 
/*     */ 
/*     */   
/*  51 */   protected int decryptBlockSize = -1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AsymmetricCrypto(AsymmetricAlgorithm algorithm) {
/*  61 */     this(algorithm, (byte[])null, (byte[])null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AsymmetricCrypto(String algorithm) {
/*  71 */     this(algorithm, (byte[])null, (byte[])null);
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
/*     */   public AsymmetricCrypto(AsymmetricAlgorithm algorithm, String privateKeyStr, String publicKeyStr) {
/*  83 */     this(algorithm.getValue(), SecureUtil.decode(privateKeyStr), SecureUtil.decode(publicKeyStr));
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
/*     */   public AsymmetricCrypto(AsymmetricAlgorithm algorithm, byte[] privateKey, byte[] publicKey) {
/*  95 */     this(algorithm.getValue(), privateKey, publicKey);
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
/*     */   public AsymmetricCrypto(AsymmetricAlgorithm algorithm, PrivateKey privateKey, PublicKey publicKey) {
/* 108 */     this(algorithm.getValue(), privateKey, publicKey);
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
/*     */   public AsymmetricCrypto(String algorithm, String privateKeyBase64, String publicKeyBase64) {
/* 120 */     this(algorithm, Base64.decode(privateKeyBase64), Base64.decode(publicKeyBase64));
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
/*     */   public AsymmetricCrypto(String algorithm, byte[] privateKey, byte[] publicKey) {
/* 134 */     this(algorithm, 
/* 135 */         KeyUtil.generatePrivateKey(algorithm, privateKey), 
/* 136 */         KeyUtil.generatePublicKey(algorithm, publicKey));
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
/*     */ 
/*     */   
/*     */   public AsymmetricCrypto(String algorithm, PrivateKey privateKey, PublicKey publicKey) {
/* 152 */     super(algorithm, privateKey, publicKey);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getEncryptBlockSize() {
/* 162 */     return this.encryptBlockSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setEncryptBlockSize(int encryptBlockSize) {
/* 171 */     this.encryptBlockSize = encryptBlockSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getDecryptBlockSize() {
/* 180 */     return this.decryptBlockSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDecryptBlockSize(int decryptBlockSize) {
/* 189 */     this.decryptBlockSize = decryptBlockSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AlgorithmParameterSpec getAlgorithmParameterSpec() {
/* 200 */     return this.cipherWrapper.getParams();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAlgorithmParameterSpec(AlgorithmParameterSpec algorithmParameterSpec) {
/* 211 */     this.cipherWrapper.setParams(algorithmParameterSpec);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AsymmetricCrypto setRandom(SecureRandom random) {
/* 222 */     this.cipherWrapper.setRandom(random);
/* 223 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public AsymmetricCrypto init(String algorithm, PrivateKey privateKey, PublicKey publicKey) {
/* 228 */     super.init(algorithm, privateKey, publicKey);
/* 229 */     initCipher();
/* 230 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] encrypt(byte[] data, KeyType keyType) {
/* 237 */     Key key = getKeyByType(keyType);
/* 238 */     this.lock.lock();
/*     */     try {
/* 240 */       Cipher cipher = initMode(1, key);
/*     */       
/* 242 */       if (this.encryptBlockSize < 0) {
/*     */         
/* 244 */         int blockSize = cipher.getBlockSize();
/* 245 */         if (blockSize > 0) {
/* 246 */           this.encryptBlockSize = blockSize;
/*     */         }
/*     */       } 
/*     */       
/* 250 */       return doFinal(data, (this.encryptBlockSize < 0) ? data.length : this.encryptBlockSize);
/* 251 */     } catch (Exception e) {
/* 252 */       throw new CryptoException(e);
/*     */     } finally {
/* 254 */       this.lock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] decrypt(byte[] data, KeyType keyType) {
/* 262 */     Key key = getKeyByType(keyType);
/* 263 */     this.lock.lock();
/*     */     try {
/* 265 */       Cipher cipher = initMode(2, key);
/*     */       
/* 267 */       if (this.decryptBlockSize < 0) {
/*     */         
/* 269 */         int blockSize = cipher.getBlockSize();
/* 270 */         if (blockSize > 0) {
/* 271 */           this.decryptBlockSize = blockSize;
/*     */         }
/*     */       } 
/*     */       
/* 275 */       return doFinal(data, (this.decryptBlockSize < 0) ? data.length : this.decryptBlockSize);
/* 276 */     } catch (Exception e) {
/* 277 */       throw new CryptoException(e);
/*     */     } finally {
/* 279 */       this.lock.unlock();
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
/*     */   
/*     */   public Cipher getCipher() {
/* 292 */     return this.cipherWrapper.getCipher();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void initCipher() {
/* 301 */     this.cipherWrapper = new CipherWrapper(this.algorithm);
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
/*     */   
/*     */   private byte[] doFinal(byte[] data, int maxBlockSize) throws IllegalBlockSizeException, BadPaddingException, IOException {
/* 316 */     int dataLength = data.length;
/*     */ 
/*     */     
/* 319 */     if (dataLength <= maxBlockSize) {
/* 320 */       return getCipher().doFinal(data, 0, dataLength);
/*     */     }
/*     */ 
/*     */     
/* 324 */     return doFinalWithBlock(data, maxBlockSize);
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
/*     */   private byte[] doFinalWithBlock(byte[] data, int maxBlockSize) throws IllegalBlockSizeException, BadPaddingException, IOException {
/* 338 */     int dataLength = data.length;
/* 339 */     FastByteArrayOutputStream out = new FastByteArrayOutputStream();
/*     */     
/* 341 */     int offSet = 0;
/*     */     
/* 343 */     int remainLength = dataLength;
/*     */ 
/*     */     
/* 346 */     while (remainLength > 0) {
/* 347 */       int blockSize = Math.min(remainLength, maxBlockSize);
/* 348 */       out.write(getCipher().doFinal(data, offSet, blockSize));
/*     */       
/* 350 */       offSet += blockSize;
/* 351 */       remainLength = dataLength - offSet;
/*     */     } 
/*     */     
/* 354 */     return out.toByteArray();
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
/*     */   private Cipher initMode(int mode, Key key) throws InvalidAlgorithmParameterException, InvalidKeyException {
/* 367 */     return this.cipherWrapper.initMode(mode, key).getCipher();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\crypto\asymmetric\AsymmetricCrypto.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */