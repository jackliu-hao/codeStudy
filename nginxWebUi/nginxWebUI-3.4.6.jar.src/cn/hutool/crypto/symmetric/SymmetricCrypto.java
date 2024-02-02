/*     */ package cn.hutool.crypto.symmetric;
/*     */ 
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import cn.hutool.core.lang.Opt;
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import cn.hutool.core.util.HexUtil;
/*     */ import cn.hutool.core.util.RandomUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.crypto.CipherMode;
/*     */ import cn.hutool.crypto.CipherWrapper;
/*     */ import cn.hutool.crypto.CryptoException;
/*     */ import cn.hutool.crypto.KeyUtil;
/*     */ import cn.hutool.crypto.Padding;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.Serializable;
/*     */ import java.security.InvalidAlgorithmParameterException;
/*     */ import java.security.InvalidKeyException;
/*     */ import java.security.SecureRandom;
/*     */ import java.security.spec.AlgorithmParameterSpec;
/*     */ import java.util.concurrent.locks.Lock;
/*     */ import java.util.concurrent.locks.ReentrantLock;
/*     */ import javax.crypto.Cipher;
/*     */ import javax.crypto.CipherInputStream;
/*     */ import javax.crypto.CipherOutputStream;
/*     */ import javax.crypto.SecretKey;
/*     */ import javax.crypto.spec.IvParameterSpec;
/*     */ import javax.crypto.spec.PBEParameterSpec;
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
/*     */ public class SymmetricCrypto
/*     */   implements SymmetricEncryptor, SymmetricDecryptor, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private CipherWrapper cipherWrapper;
/*     */   private SecretKey secretKey;
/*     */   private boolean isZeroPadding;
/*  54 */   private final Lock lock = new ReentrantLock();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SymmetricCrypto(SymmetricAlgorithm algorithm) {
/*  64 */     this(algorithm, (byte[])null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SymmetricCrypto(String algorithm) {
/*  73 */     this(algorithm, (byte[])null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SymmetricCrypto(SymmetricAlgorithm algorithm, byte[] key) {
/*  83 */     this(algorithm.getValue(), key);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SymmetricCrypto(SymmetricAlgorithm algorithm, SecretKey key) {
/*  94 */     this(algorithm.getValue(), key);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SymmetricCrypto(String algorithm, byte[] key) {
/* 104 */     this(algorithm, KeyUtil.generateKey(algorithm, key));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SymmetricCrypto(String algorithm, SecretKey key) {
/* 115 */     this(algorithm, key, null);
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
/*     */   public SymmetricCrypto(String algorithm, SecretKey key, AlgorithmParameterSpec paramsSpec) {
/* 127 */     init(algorithm, key);
/* 128 */     initParams(algorithm, paramsSpec);
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
/*     */   public SymmetricCrypto init(String algorithm, SecretKey key) {
/* 141 */     Assert.notBlank(algorithm, "'algorithm' must be not blank !", new Object[0]);
/* 142 */     this.secretKey = key;
/*     */ 
/*     */     
/* 145 */     if (algorithm.contains(Padding.ZeroPadding.name())) {
/* 146 */       algorithm = StrUtil.replace(algorithm, Padding.ZeroPadding.name(), Padding.NoPadding.name());
/* 147 */       this.isZeroPadding = true;
/*     */     } 
/*     */     
/* 150 */     this.cipherWrapper = new CipherWrapper(algorithm);
/* 151 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SecretKey getSecretKey() {
/* 160 */     return this.secretKey;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Cipher getCipher() {
/* 169 */     return this.cipherWrapper.getCipher();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SymmetricCrypto setParams(AlgorithmParameterSpec params) {
/* 179 */     this.cipherWrapper.setParams(params);
/* 180 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SymmetricCrypto setIv(IvParameterSpec iv) {
/* 190 */     return setParams(iv);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SymmetricCrypto setIv(byte[] iv) {
/* 200 */     return setIv(new IvParameterSpec(iv));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SymmetricCrypto setRandom(SecureRandom random) {
/* 211 */     this.cipherWrapper.setRandom(random);
/* 212 */     return this;
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
/*     */   public SymmetricCrypto setMode(CipherMode mode) {
/* 225 */     this.lock.lock();
/*     */     try {
/* 227 */       initMode(mode.getValue());
/* 228 */     } catch (Exception e) {
/* 229 */       throw new CryptoException(e);
/*     */     } finally {
/* 231 */       this.lock.unlock();
/*     */     } 
/* 233 */     return this;
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
/*     */   public byte[] update(byte[] data) {
/* 245 */     Cipher cipher = this.cipherWrapper.getCipher();
/* 246 */     this.lock.lock();
/*     */     try {
/* 248 */       return cipher.update(paddingDataWithZero(data, cipher.getBlockSize()));
/* 249 */     } catch (Exception e) {
/* 250 */       throw new CryptoException(e);
/*     */     } finally {
/* 252 */       this.lock.unlock();
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
/*     */   public String updateHex(byte[] data) {
/* 265 */     return HexUtil.encodeHexStr(update(data));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] encrypt(byte[] data) {
/* 272 */     this.lock.lock();
/*     */     try {
/* 274 */       Cipher cipher = initMode(1);
/* 275 */       return cipher.doFinal(paddingDataWithZero(data, cipher.getBlockSize()));
/* 276 */     } catch (Exception e) {
/* 277 */       throw new CryptoException(e);
/*     */     } finally {
/* 279 */       this.lock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void encrypt(InputStream data, OutputStream out, boolean isClose) throws IORuntimeException {
/* 285 */     this.lock.lock();
/* 286 */     CipherOutputStream cipherOutputStream = null;
/*     */     try {
/* 288 */       Cipher cipher = initMode(1);
/* 289 */       cipherOutputStream = new CipherOutputStream(out, cipher);
/* 290 */       long length = IoUtil.copy(data, cipherOutputStream);
/* 291 */       if (this.isZeroPadding) {
/* 292 */         int blockSize = cipher.getBlockSize();
/* 293 */         if (blockSize > 0) {
/*     */           
/* 295 */           int remainLength = (int)(length % blockSize);
/* 296 */           if (remainLength > 0) {
/*     */             
/* 298 */             cipherOutputStream.write(new byte[blockSize - remainLength]);
/* 299 */             cipherOutputStream.flush();
/*     */           } 
/*     */         } 
/*     */       } 
/* 303 */     } catch (IORuntimeException e) {
/* 304 */       throw e;
/* 305 */     } catch (Exception e) {
/* 306 */       throw new CryptoException(e);
/*     */     } finally {
/* 308 */       this.lock.unlock();
/*     */ 
/*     */       
/* 311 */       IoUtil.close(cipherOutputStream);
/* 312 */       if (isClose) {
/* 313 */         IoUtil.close(data);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] decrypt(byte[] bytes) {
/*     */     int blockSize;
/*     */     byte[] decryptData;
/* 325 */     this.lock.lock();
/*     */     try {
/* 327 */       Cipher cipher = initMode(2);
/* 328 */       blockSize = cipher.getBlockSize();
/* 329 */       decryptData = cipher.doFinal(bytes);
/* 330 */     } catch (Exception e) {
/* 331 */       throw new CryptoException(e);
/*     */     } finally {
/* 333 */       this.lock.unlock();
/*     */     } 
/*     */     
/* 336 */     return removePadding(decryptData, blockSize);
/*     */   }
/*     */ 
/*     */   
/*     */   public void decrypt(InputStream data, OutputStream out, boolean isClose) throws IORuntimeException {
/* 341 */     this.lock.lock();
/* 342 */     CipherInputStream cipherInputStream = null;
/*     */     try {
/* 344 */       Cipher cipher = initMode(2);
/* 345 */       cipherInputStream = new CipherInputStream(data, cipher);
/* 346 */       if (this.isZeroPadding) {
/* 347 */         int blockSize = cipher.getBlockSize();
/* 348 */         if (blockSize > 0) {
/* 349 */           copyForZeroPadding(cipherInputStream, out, blockSize);
/*     */           return;
/*     */         } 
/*     */       } 
/* 353 */       IoUtil.copy(cipherInputStream, out);
/* 354 */     } catch (IOException e) {
/* 355 */       throw new IORuntimeException(e);
/* 356 */     } catch (IORuntimeException e) {
/* 357 */       throw e;
/* 358 */     } catch (Exception e) {
/* 359 */       throw new CryptoException(e);
/*     */     } finally {
/* 361 */       this.lock.unlock();
/*     */ 
/*     */       
/* 364 */       IoUtil.close(cipherInputStream);
/* 365 */       if (isClose) {
/* 366 */         IoUtil.close(data);
/*     */       }
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private SymmetricCrypto initParams(String algorithm, AlgorithmParameterSpec paramsSpec) {
/* 384 */     if (null == paramsSpec) {
/*     */       
/* 386 */       byte[] iv = (byte[])Opt.ofNullable(this.cipherWrapper).map(CipherWrapper::getCipher).map(Cipher::getIV).get();
/*     */ 
/*     */       
/* 389 */       if (StrUtil.startWithIgnoreCase(algorithm, "PBE")) {
/*     */         
/* 391 */         if (null == iv) {
/* 392 */           iv = RandomUtil.randomBytes(8);
/*     */         }
/* 394 */         paramsSpec = new PBEParameterSpec(iv, 100);
/* 395 */       } else if (StrUtil.startWithIgnoreCase(algorithm, "AES") && 
/* 396 */         null != iv) {
/*     */         
/* 398 */         paramsSpec = new IvParameterSpec(iv);
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 403 */     return setParams(paramsSpec);
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
/*     */   private Cipher initMode(int mode) throws InvalidKeyException, InvalidAlgorithmParameterException {
/* 415 */     return this.cipherWrapper.initMode(mode, this.secretKey).getCipher();
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
/*     */ 
/*     */   
/*     */   private byte[] paddingDataWithZero(byte[] data, int blockSize) {
/* 433 */     if (this.isZeroPadding) {
/* 434 */       int length = data.length;
/*     */       
/* 436 */       int remainLength = length % blockSize;
/* 437 */       if (remainLength > 0)
/*     */       {
/* 439 */         return ArrayUtil.resize(data, length + blockSize - remainLength);
/*     */       }
/*     */     } 
/* 442 */     return data;
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
/*     */   private byte[] removePadding(byte[] data, int blockSize) {
/* 457 */     if (this.isZeroPadding && blockSize > 0) {
/* 458 */       int length = data.length;
/* 459 */       int remainLength = length % blockSize;
/* 460 */       if (remainLength == 0) {
/*     */         
/* 462 */         int i = length - 1;
/* 463 */         while (i >= 0 && 0 == data[i]) {
/* 464 */           i--;
/*     */         }
/* 466 */         return ArrayUtil.resize(data, i + 1);
/*     */       } 
/*     */     } 
/* 469 */     return data;
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
/*     */   private static void copyForZeroPadding(CipherInputStream in, OutputStream out, int blockSize) throws IOException {
/* 481 */     int n = 1;
/* 482 */     if (8192 > blockSize) {
/* 483 */       n = Math.max(n, 8192 / blockSize);
/*     */     }
/*     */     
/* 486 */     int bufSize = blockSize * n;
/* 487 */     byte[] preBuffer = new byte[bufSize];
/* 488 */     byte[] buffer = new byte[bufSize];
/*     */     
/* 490 */     boolean isFirst = true;
/* 491 */     int preReadSize = 0; int readSize;
/* 492 */     while ((readSize = in.read(buffer)) != -1) {
/* 493 */       if (isFirst) {
/* 494 */         isFirst = false;
/*     */       } else {
/*     */         
/* 497 */         out.write(preBuffer, 0, preReadSize);
/*     */       } 
/* 499 */       ArrayUtil.copy(buffer, preBuffer, readSize);
/* 500 */       preReadSize = readSize;
/*     */     } 
/*     */     
/* 503 */     int i = preReadSize - 1;
/* 504 */     while (i >= 0 && 0 == preBuffer[i]) {
/* 505 */       i--;
/*     */     }
/* 507 */     out.write(preBuffer, 0, i + 1);
/* 508 */     out.flush();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\crypto\symmetric\SymmetricCrypto.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */