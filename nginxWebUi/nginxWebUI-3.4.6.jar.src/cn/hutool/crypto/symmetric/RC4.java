/*     */ package cn.hutool.crypto.symmetric;
/*     */ 
/*     */ import cn.hutool.core.codec.Base64;
/*     */ import cn.hutool.core.util.CharsetUtil;
/*     */ import cn.hutool.core.util.HexUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.crypto.CryptoException;
/*     */ import cn.hutool.crypto.SecureUtil;
/*     */ import java.io.Serializable;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.concurrent.locks.ReentrantReadWriteLock;
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
/*     */ public class RC4
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private static final int SBOX_LENGTH = 256;
/*     */   private static final int KEY_MIN_LENGTH = 5;
/*     */   private int[] sbox;
/*  33 */   private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RC4(String key) throws CryptoException {
/*  42 */     setKey(key);
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
/*     */   public byte[] encrypt(String message, Charset charset) throws CryptoException {
/*  54 */     return crypt(StrUtil.bytes(message, charset));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] encrypt(String message) throws CryptoException {
/*  65 */     return encrypt(message, CharsetUtil.CHARSET_UTF_8);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String encryptHex(byte[] data) {
/*  76 */     return HexUtil.encodeHexStr(crypt(data));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String encryptBase64(byte[] data) {
/*  87 */     return Base64.encode(crypt(data));
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
/*     */   public String encryptHex(String data, Charset charset) {
/*  99 */     return HexUtil.encodeHexStr(encrypt(data, charset));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String encryptHex(String data) {
/* 110 */     return HexUtil.encodeHexStr(encrypt(data));
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
/*     */   public String encryptBase64(String data, Charset charset) {
/* 122 */     return Base64.encode(encrypt(data, charset));
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
/*     */   public String encryptBase64(String data) {
/* 134 */     return Base64.encode(encrypt(data));
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
/*     */   public String decrypt(byte[] message, Charset charset) throws CryptoException {
/* 146 */     return StrUtil.str(crypt(message), charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String decrypt(byte[] message) throws CryptoException {
/* 157 */     return decrypt(message, CharsetUtil.CHARSET_UTF_8);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String decrypt(String message) {
/* 168 */     return decrypt(SecureUtil.decode(message));
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
/*     */   public String decrypt(String message, Charset charset) {
/* 180 */     return StrUtil.str(decrypt(message), charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] crypt(byte[] msg) {
/*     */     byte[] code;
/* 191 */     ReentrantReadWriteLock.ReadLock readLock = this.lock.readLock();
/*     */     
/* 193 */     readLock.lock();
/*     */     try {
/* 195 */       int[] sbox = (int[])this.sbox.clone();
/* 196 */       code = new byte[msg.length];
/* 197 */       int i = 0;
/* 198 */       int j = 0;
/* 199 */       for (int n = 0; n < msg.length; n++) {
/* 200 */         i = (i + 1) % 256;
/* 201 */         j = (j + sbox[i]) % 256;
/* 202 */         swap(i, j, sbox);
/* 203 */         int rand = sbox[(sbox[i] + sbox[j]) % 256];
/* 204 */         code[n] = (byte)(rand ^ msg[n]);
/*     */       } 
/*     */     } finally {
/* 207 */       readLock.unlock();
/*     */     } 
/* 209 */     return code;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setKey(String key) throws CryptoException {
/* 219 */     int length = key.length();
/* 220 */     if (length < 5 || length >= 256) {
/* 221 */       throw new CryptoException("Key length has to be between {} and {}", new Object[] { Integer.valueOf(5), Integer.valueOf(255) });
/*     */     }
/*     */     
/* 224 */     ReentrantReadWriteLock.WriteLock writeLock = this.lock.writeLock();
/* 225 */     writeLock.lock();
/*     */     try {
/* 227 */       this.sbox = initSBox(StrUtil.utf8Bytes(key));
/*     */     } finally {
/* 229 */       writeLock.unlock();
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
/*     */   private int[] initSBox(byte[] key) {
/* 241 */     int[] sbox = new int[256];
/* 242 */     int j = 0;
/*     */     int i;
/* 244 */     for (i = 0; i < 256; i++) {
/* 245 */       sbox[i] = i;
/*     */     }
/*     */     
/* 248 */     for (i = 0; i < 256; i++) {
/* 249 */       j = (j + sbox[i] + key[i % key.length] & 0xFF) % 256;
/* 250 */       swap(i, j, sbox);
/*     */     } 
/* 252 */     return sbox;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void swap(int i, int j, int[] sbox) {
/* 263 */     int temp = sbox[i];
/* 264 */     sbox[i] = sbox[j];
/* 265 */     sbox[j] = temp;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\crypto\symmetric\RC4.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */