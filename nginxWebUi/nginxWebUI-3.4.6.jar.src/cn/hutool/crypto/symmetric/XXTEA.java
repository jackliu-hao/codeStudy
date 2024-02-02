/*     */ package cn.hutool.crypto.symmetric;
/*     */ 
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.Serializable;
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
/*     */ public class XXTEA
/*     */   implements SymmetricEncryptor, SymmetricDecryptor, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private static final int DELTA = -1640531527;
/*     */   private final byte[] key;
/*     */   
/*     */   public XXTEA(byte[] key) {
/*  28 */     this.key = key;
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] encrypt(byte[] data) {
/*  33 */     if (data.length == 0) {
/*  34 */       return data;
/*     */     }
/*  36 */     return toByteArray(encrypt(
/*  37 */           toIntArray(data, true), 
/*  38 */           toIntArray(fixKey(this.key), false)), false);
/*     */   }
/*     */ 
/*     */   
/*     */   public void encrypt(InputStream data, OutputStream out, boolean isClose) {
/*  43 */     IoUtil.write(out, isClose, encrypt(IoUtil.readBytes(data)));
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] decrypt(byte[] data) {
/*  48 */     if (data.length == 0) {
/*  49 */       return data;
/*     */     }
/*  51 */     return toByteArray(decrypt(
/*  52 */           toIntArray(data, false), 
/*  53 */           toIntArray(fixKey(this.key), false)), true);
/*     */   }
/*     */ 
/*     */   
/*     */   public void decrypt(InputStream data, OutputStream out, boolean isClose) {
/*  58 */     IoUtil.write(out, isClose, decrypt(IoUtil.readBytes(data)));
/*     */   }
/*     */ 
/*     */   
/*     */   private static int[] encrypt(int[] v, int[] k) {
/*  63 */     int n = v.length - 1;
/*     */     
/*  65 */     if (n < 1) {
/*  66 */       return v;
/*     */     }
/*  68 */     int q = 6 + 52 / (n + 1);
/*  69 */     int z = v[n], sum = 0;
/*     */     
/*  71 */     while (q-- > 0) {
/*  72 */       sum += -1640531527;
/*  73 */       int e = sum >>> 2 & 0x3; int p;
/*  74 */       for (p = 0; p < n; p++) {
/*  75 */         int i = v[p + 1];
/*  76 */         z = v[p] = v[p] + mx(sum, i, z, p, e, k);
/*     */       } 
/*  78 */       int y = v[0];
/*  79 */       z = v[n] = v[n] + mx(sum, y, z, p, e, k);
/*     */     } 
/*  81 */     return v;
/*     */   }
/*     */   
/*     */   private static int[] decrypt(int[] v, int[] k) {
/*  85 */     int n = v.length - 1;
/*     */     
/*  87 */     if (n < 1) {
/*  88 */       return v;
/*     */     }
/*  90 */     int q = 6 + 52 / (n + 1);
/*  91 */     int y = v[0], sum = q * -1640531527;
/*     */     
/*  93 */     while (sum != 0) {
/*  94 */       int e = sum >>> 2 & 0x3; int p;
/*  95 */       for (p = n; p > 0; p--) {
/*  96 */         int i = v[p - 1];
/*  97 */         y = v[p] = v[p] - mx(sum, y, i, p, e, k);
/*     */       } 
/*  99 */       int z = v[n];
/* 100 */       y = v[0] = v[0] - mx(sum, y, z, p, e, k);
/* 101 */       sum -= -1640531527;
/*     */     } 
/* 103 */     return v;
/*     */   }
/*     */   
/*     */   private static int mx(int sum, int y, int z, int p, int e, int[] k) {
/* 107 */     return (z >>> 5 ^ y << 2) + (y >>> 3 ^ z << 4) ^ (sum ^ y) + (k[p & 0x3 ^ e] ^ z);
/*     */   }
/*     */   
/*     */   private static byte[] fixKey(byte[] key) {
/* 111 */     if (key.length == 16) {
/* 112 */       return key;
/*     */     }
/* 114 */     byte[] fixedkey = new byte[16];
/* 115 */     System.arraycopy(key, 0, fixedkey, 0, Math.min(key.length, 16));
/* 116 */     return fixedkey;
/*     */   }
/*     */   
/*     */   private static int[] toIntArray(byte[] data, boolean includeLength) {
/* 120 */     int result[], n = ((data.length & 0x3) == 0) ? (data.length >>> 2) : ((data.length >>> 2) + 1);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 125 */     if (includeLength) {
/* 126 */       result = new int[n + 1];
/* 127 */       result[n] = data.length;
/*     */     } else {
/* 129 */       result = new int[n];
/*     */     } 
/* 131 */     n = data.length;
/* 132 */     for (int i = 0; i < n; i++) {
/* 133 */       result[i >>> 2] = result[i >>> 2] | (0xFF & data[i]) << (i & 0x3) << 3;
/*     */     }
/* 135 */     return result;
/*     */   }
/*     */   
/*     */   private static byte[] toByteArray(int[] data, boolean includeLength) {
/* 139 */     int n = data.length << 2;
/*     */     
/* 141 */     if (includeLength) {
/* 142 */       int m = data[data.length - 1];
/* 143 */       n -= 4;
/* 144 */       if (m < n - 3 || m > n) {
/* 145 */         return null;
/*     */       }
/* 147 */       n = m;
/*     */     } 
/* 149 */     byte[] result = new byte[n];
/*     */     
/* 151 */     for (int i = 0; i < n; i++) {
/* 152 */       result[i] = (byte)(data[i >>> 2] >>> (i & 0x3) << 3);
/*     */     }
/* 154 */     return result;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\crypto\symmetric\XXTEA.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */