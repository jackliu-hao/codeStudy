/*     */ package cn.hutool.crypto.asymmetric;
/*     */ 
/*     */ import cn.hutool.core.codec.Base64;
/*     */ import cn.hutool.core.collection.CollUtil;
/*     */ import cn.hutool.core.util.CharsetUtil;
/*     */ import cn.hutool.core.util.HexUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.crypto.CryptoException;
/*     */ import cn.hutool.crypto.SecureUtil;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.InputStream;
/*     */ import java.nio.charset.Charset;
/*     */ import java.security.InvalidAlgorithmParameterException;
/*     */ import java.security.KeyPair;
/*     */ import java.security.PrivateKey;
/*     */ import java.security.PublicKey;
/*     */ import java.security.Signature;
/*     */ import java.security.cert.Certificate;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.security.spec.AlgorithmParameterSpec;
/*     */ import java.util.Set;
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
/*     */ public class Sign
/*     */   extends BaseAsymmetric<Sign>
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected Signature signature;
/*     */   
/*     */   public Sign(SignAlgorithm algorithm) {
/*  44 */     this(algorithm, (byte[])null, (byte[])null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Sign(String algorithm) {
/*  53 */     this(algorithm, (byte[])null, (byte[])null);
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
/*     */   public Sign(SignAlgorithm algorithm, String privateKeyStr, String publicKeyStr) {
/*  65 */     this(algorithm.getValue(), SecureUtil.decode(privateKeyStr), SecureUtil.decode(publicKeyStr));
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
/*     */   public Sign(SignAlgorithm algorithm, byte[] privateKey, byte[] publicKey) {
/*  77 */     this(algorithm.getValue(), privateKey, publicKey);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Sign(SignAlgorithm algorithm, KeyPair keyPair) {
/*  88 */     this(algorithm.getValue(), keyPair);
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
/*     */   public Sign(SignAlgorithm algorithm, PrivateKey privateKey, PublicKey publicKey) {
/* 100 */     this(algorithm.getValue(), privateKey, publicKey);
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
/*     */   public Sign(String algorithm, String privateKeyBase64, String publicKeyBase64) {
/* 112 */     this(algorithm, Base64.decode(privateKeyBase64), Base64.decode(publicKeyBase64));
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
/*     */   public Sign(String algorithm, byte[] privateKey, byte[] publicKey) {
/* 126 */     this(algorithm, 
/* 127 */         SecureUtil.generatePrivateKey(algorithm, privateKey), 
/* 128 */         SecureUtil.generatePublicKey(algorithm, publicKey));
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
/*     */   public Sign(String algorithm, KeyPair keyPair) {
/* 140 */     this(algorithm, keyPair.getPrivate(), keyPair.getPublic());
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
/*     */   public Sign(String algorithm, PrivateKey privateKey, PublicKey publicKey) {
/* 154 */     super(algorithm, privateKey, publicKey);
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
/*     */   public Sign init(String algorithm, PrivateKey privateKey, PublicKey publicKey) {
/* 168 */     this.signature = SecureUtil.createSignature(algorithm);
/* 169 */     super.init(algorithm, privateKey, publicKey);
/* 170 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Sign setParameter(AlgorithmParameterSpec params) {
/*     */     try {
/* 182 */       this.signature.setParameter(params);
/* 183 */     } catch (InvalidAlgorithmParameterException e) {
/* 184 */       throw new CryptoException(e);
/*     */     } 
/* 186 */     return this;
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
/*     */   public byte[] sign(String data, Charset charset) {
/* 199 */     return sign(StrUtil.bytes(data, charset));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] sign(String data) {
/* 210 */     return sign(data, CharsetUtil.CHARSET_UTF_8);
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
/*     */   public String signHex(String data, Charset charset) {
/* 222 */     return HexUtil.encodeHexStr(sign(data, charset));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String signHex(String data) {
/* 233 */     return signHex(data, CharsetUtil.CHARSET_UTF_8);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] sign(byte[] data) {
/* 243 */     return sign(new ByteArrayInputStream(data), -1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String signHex(byte[] data) {
/* 254 */     return HexUtil.encodeHexStr(sign(data));
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
/*     */   public String signHex(InputStream data) {
/* 266 */     return HexUtil.encodeHexStr(sign(data));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] sign(InputStream data) {
/* 277 */     return sign(data, 8192);
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
/*     */   public String digestHex(InputStream data, int bufferLength) {
/* 290 */     return HexUtil.encodeHexStr(sign(data, bufferLength));
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
/*     */   public byte[] sign(InputStream data, int bufferLength) {
/* 302 */     if (bufferLength < 1) {
/* 303 */       bufferLength = 8192;
/*     */     }
/*     */     
/* 306 */     byte[] buffer = new byte[bufferLength];
/* 307 */     this.lock.lock(); try {
/*     */       byte[] result;
/* 309 */       this.signature.initSign(this.privateKey);
/*     */       
/*     */       try {
/* 312 */         int read = data.read(buffer, 0, bufferLength);
/* 313 */         while (read > -1) {
/* 314 */           this.signature.update(buffer, 0, read);
/* 315 */           read = data.read(buffer, 0, bufferLength);
/*     */         } 
/* 317 */         result = this.signature.sign();
/* 318 */       } catch (Exception e) {
/* 319 */         throw new CryptoException(e);
/*     */       } 
/* 321 */       return result;
/* 322 */     } catch (Exception e) {
/* 323 */       byte[] result; throw new CryptoException(result);
/*     */     } finally {
/* 325 */       this.lock.unlock();
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
/*     */   public boolean verify(byte[] data, byte[] sign) {
/* 337 */     this.lock.lock();
/*     */     try {
/* 339 */       this.signature.initVerify(this.publicKey);
/* 340 */       this.signature.update(data);
/* 341 */       return this.signature.verify(sign);
/* 342 */     } catch (Exception e) {
/* 343 */       throw new CryptoException(e);
/*     */     } finally {
/* 345 */       this.lock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Signature getSignature() {
/* 355 */     return this.signature;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Sign setSignature(Signature signature) {
/* 365 */     this.signature = signature;
/* 366 */     return this;
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
/*     */   public Sign setCertificate(Certificate certificate) {
/* 380 */     if (certificate instanceof X509Certificate) {
/*     */ 
/*     */ 
/*     */       
/* 384 */       X509Certificate cert = (X509Certificate)certificate;
/* 385 */       Set<String> critSet = cert.getCriticalExtensionOIDs();
/*     */       
/* 387 */       if (CollUtil.isNotEmpty(critSet) && critSet.contains("2.5.29.15")) {
/* 388 */         boolean[] keyUsageInfo = cert.getKeyUsage();
/*     */         
/* 390 */         if (keyUsageInfo != null && !keyUsageInfo[0]) {
/* 391 */           throw new CryptoException("Wrong key usage");
/*     */         }
/*     */       } 
/*     */     } 
/* 395 */     this.publicKey = certificate.getPublicKey();
/* 396 */     return this;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\crypto\asymmetric\Sign.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */