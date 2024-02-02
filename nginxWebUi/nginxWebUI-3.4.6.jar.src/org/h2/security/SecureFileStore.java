/*     */ package org.h2.security;
/*     */ 
/*     */ import org.h2.store.DataHandler;
/*     */ import org.h2.store.FileStore;
/*     */ import org.h2.util.Bits;
/*     */ import org.h2.util.MathUtils;
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
/*     */ public class SecureFileStore
/*     */   extends FileStore
/*     */ {
/*     */   private byte[] key;
/*     */   private final BlockCipher cipher;
/*     */   private final BlockCipher cipherForInitVector;
/*  24 */   private byte[] buffer = new byte[4];
/*     */   
/*     */   private long pos;
/*     */   private final byte[] bufferForInitVector;
/*     */   private final int keyIterations;
/*     */   
/*     */   public SecureFileStore(DataHandler paramDataHandler, String paramString1, String paramString2, String paramString3, byte[] paramArrayOfbyte, int paramInt) {
/*  31 */     super(paramDataHandler, paramString1, paramString2);
/*  32 */     this.key = paramArrayOfbyte;
/*  33 */     this.cipher = CipherFactory.getBlockCipher(paramString3);
/*  34 */     this.cipherForInitVector = CipherFactory.getBlockCipher(paramString3);
/*  35 */     this.keyIterations = paramInt;
/*  36 */     this.bufferForInitVector = new byte[16];
/*     */   }
/*     */ 
/*     */   
/*     */   protected byte[] generateSalt() {
/*  41 */     return MathUtils.secureRandomBytes(16);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void initKey(byte[] paramArrayOfbyte) {
/*  46 */     this.key = SHA256.getHashWithSalt(this.key, paramArrayOfbyte);
/*  47 */     for (byte b = 0; b < this.keyIterations; b++) {
/*  48 */       this.key = SHA256.getHash(this.key, true);
/*     */     }
/*  50 */     this.cipher.setKey(this.key);
/*  51 */     this.key = SHA256.getHash(this.key, true);
/*  52 */     this.cipherForInitVector.setKey(this.key);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void writeDirect(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
/*  57 */     super.write(paramArrayOfbyte, paramInt1, paramInt2);
/*  58 */     this.pos += paramInt2;
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
/*  63 */     if (this.buffer.length < paramArrayOfbyte.length) {
/*  64 */       this.buffer = new byte[paramInt2];
/*     */     }
/*  66 */     System.arraycopy(paramArrayOfbyte, paramInt1, this.buffer, 0, paramInt2);
/*  67 */     xorInitVector(this.buffer, 0, paramInt2, this.pos);
/*  68 */     this.cipher.encrypt(this.buffer, 0, paramInt2);
/*  69 */     super.write(this.buffer, 0, paramInt2);
/*  70 */     this.pos += paramInt2;
/*     */   }
/*     */ 
/*     */   
/*     */   public void readFullyDirect(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
/*  75 */     super.readFully(paramArrayOfbyte, paramInt1, paramInt2);
/*  76 */     this.pos += paramInt2;
/*     */   }
/*     */ 
/*     */   
/*     */   public void readFully(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
/*  81 */     super.readFully(paramArrayOfbyte, paramInt1, paramInt2);
/*  82 */     for (byte b = 0; b < paramInt2; b++) {
/*  83 */       if (paramArrayOfbyte[b] != 0) {
/*  84 */         this.cipher.decrypt(paramArrayOfbyte, paramInt1, paramInt2);
/*  85 */         xorInitVector(paramArrayOfbyte, paramInt1, paramInt2, this.pos);
/*     */         break;
/*     */       } 
/*     */     } 
/*  89 */     this.pos += paramInt2;
/*     */   }
/*     */ 
/*     */   
/*     */   public void seek(long paramLong) {
/*  94 */     this.pos = paramLong;
/*  95 */     super.seek(paramLong);
/*     */   }
/*     */   
/*     */   private void xorInitVector(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, long paramLong) {
/*  99 */     byte[] arrayOfByte = this.bufferForInitVector;
/* 100 */     while (paramInt2 > 0) {
/* 101 */       byte b; for (b = 0; b < 16; b += 8) {
/* 102 */         Bits.writeLong(arrayOfByte, b, paramLong + b >>> 3L);
/*     */       }
/* 104 */       this.cipherForInitVector.encrypt(arrayOfByte, 0, 16);
/* 105 */       for (b = 0; b < 16; b++) {
/* 106 */         paramArrayOfbyte[paramInt1 + b] = (byte)(paramArrayOfbyte[paramInt1 + b] ^ arrayOfByte[b]);
/*     */       }
/* 108 */       paramLong += 16L;
/* 109 */       paramInt1 += 16;
/* 110 */       paramInt2 -= 16;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\security\SecureFileStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */