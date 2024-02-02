/*     */ package org.h2.util;
/*     */ 
/*     */ import java.util.UUID;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class Bits
/*     */ {
/*     */   public static int compareNotNull(char[] paramArrayOfchar1, char[] paramArrayOfchar2) {
/*  36 */     if (paramArrayOfchar1 == paramArrayOfchar2) {
/*  37 */       return 0;
/*     */     }
/*  39 */     int i = Math.min(paramArrayOfchar1.length, paramArrayOfchar2.length);
/*  40 */     for (byte b = 0; b < i; b++) {
/*  41 */       char c1 = paramArrayOfchar1[b];
/*  42 */       char c2 = paramArrayOfchar2[b];
/*  43 */       if (c1 != c2) {
/*  44 */         return (c1 > c2) ? 1 : -1;
/*     */       }
/*     */     } 
/*  47 */     return Integer.signum(paramArrayOfchar1.length - paramArrayOfchar2.length);
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
/*     */ 
/*     */   
/*     */   public static int compareNotNullSigned(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
/*  67 */     if (paramArrayOfbyte1 == paramArrayOfbyte2) {
/*  68 */       return 0;
/*     */     }
/*  70 */     int i = Math.min(paramArrayOfbyte1.length, paramArrayOfbyte2.length);
/*  71 */     for (byte b = 0; b < i; b++) {
/*  72 */       byte b1 = paramArrayOfbyte1[b];
/*  73 */       byte b2 = paramArrayOfbyte2[b];
/*  74 */       if (b1 != b2) {
/*  75 */         return (b1 > b2) ? 1 : -1;
/*     */       }
/*     */     } 
/*  78 */     return Integer.signum(paramArrayOfbyte1.length - paramArrayOfbyte2.length);
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
/*     */ 
/*     */   
/*     */   public static int compareNotNullUnsigned(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
/*  98 */     if (paramArrayOfbyte1 == paramArrayOfbyte2) {
/*  99 */       return 0;
/*     */     }
/* 101 */     int i = Math.min(paramArrayOfbyte1.length, paramArrayOfbyte2.length);
/* 102 */     for (byte b = 0; b < i; b++) {
/* 103 */       int j = paramArrayOfbyte1[b] & 0xFF;
/* 104 */       int k = paramArrayOfbyte2[b] & 0xFF;
/* 105 */       if (j != k) {
/* 106 */         return (j > k) ? 1 : -1;
/*     */       }
/*     */     } 
/* 109 */     return Integer.signum(paramArrayOfbyte1.length - paramArrayOfbyte2.length);
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
/*     */   public static int readInt(byte[] paramArrayOfbyte, int paramInt) {
/* 123 */     return (paramArrayOfbyte[paramInt++] << 24) + ((paramArrayOfbyte[paramInt++] & 0xFF) << 16) + ((paramArrayOfbyte[paramInt++] & 0xFF) << 8) + (paramArrayOfbyte[paramInt] & 0xFF);
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
/*     */   public static int readIntLE(byte[] paramArrayOfbyte, int paramInt) {
/* 137 */     return (paramArrayOfbyte[paramInt++] & 0xFF) + ((paramArrayOfbyte[paramInt++] & 0xFF) << 8) + ((paramArrayOfbyte[paramInt++] & 0xFF) << 16) + (paramArrayOfbyte[paramInt] << 24);
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
/*     */   public static long readLong(byte[] paramArrayOfbyte, int paramInt) {
/* 151 */     return (readInt(paramArrayOfbyte, paramInt) << 32L) + (readInt(paramArrayOfbyte, paramInt + 4) & 0xFFFFFFFFL);
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
/*     */   public static long readLongLE(byte[] paramArrayOfbyte, int paramInt) {
/* 165 */     return (readIntLE(paramArrayOfbyte, paramInt) & 0xFFFFFFFFL) + (readIntLE(paramArrayOfbyte, paramInt + 4) << 32L);
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
/*     */   public static double readDouble(byte[] paramArrayOfbyte, int paramInt) {
/* 179 */     return Double.longBitsToDouble(readLong(paramArrayOfbyte, paramInt));
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
/*     */   public static double readDoubleLE(byte[] paramArrayOfbyte, int paramInt) {
/* 193 */     return Double.longBitsToDouble(readLongLE(paramArrayOfbyte, paramInt));
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
/*     */   public static byte[] uuidToBytes(long paramLong1, long paramLong2) {
/* 206 */     byte[] arrayOfByte = new byte[16];
/* 207 */     for (byte b = 0; b < 8; b++) {
/* 208 */       arrayOfByte[b] = (byte)(int)(paramLong1 >> 8 * (7 - b) & 0xFFL);
/* 209 */       arrayOfByte[8 + b] = (byte)(int)(paramLong2 >> 8 * (7 - b) & 0xFFL);
/*     */     } 
/* 211 */     return arrayOfByte;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] uuidToBytes(UUID paramUUID) {
/* 222 */     return uuidToBytes(paramUUID.getMostSignificantBits(), paramUUID.getLeastSignificantBits());
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
/*     */   public static void writeInt(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
/* 237 */     paramArrayOfbyte[paramInt1++] = (byte)(paramInt2 >> 24);
/* 238 */     paramArrayOfbyte[paramInt1++] = (byte)(paramInt2 >> 16);
/* 239 */     paramArrayOfbyte[paramInt1++] = (byte)(paramInt2 >> 8);
/* 240 */     paramArrayOfbyte[paramInt1] = (byte)paramInt2;
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
/*     */   public static void writeIntLE(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
/* 255 */     paramArrayOfbyte[paramInt1++] = (byte)paramInt2;
/* 256 */     paramArrayOfbyte[paramInt1++] = (byte)(paramInt2 >> 8);
/* 257 */     paramArrayOfbyte[paramInt1++] = (byte)(paramInt2 >> 16);
/* 258 */     paramArrayOfbyte[paramInt1] = (byte)(paramInt2 >> 24);
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
/*     */   public static void writeLong(byte[] paramArrayOfbyte, int paramInt, long paramLong) {
/* 273 */     writeInt(paramArrayOfbyte, paramInt, (int)(paramLong >> 32L));
/* 274 */     writeInt(paramArrayOfbyte, paramInt + 4, (int)paramLong);
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
/*     */   public static void writeLongLE(byte[] paramArrayOfbyte, int paramInt, long paramLong) {
/* 289 */     writeIntLE(paramArrayOfbyte, paramInt, (int)paramLong);
/* 290 */     writeIntLE(paramArrayOfbyte, paramInt + 4, (int)(paramLong >> 32L));
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
/*     */   public static void writeDouble(byte[] paramArrayOfbyte, int paramInt, double paramDouble) {
/* 305 */     writeLong(paramArrayOfbyte, paramInt, Double.doubleToRawLongBits(paramDouble));
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
/*     */   public static void writeDoubleLE(byte[] paramArrayOfbyte, int paramInt, double paramDouble) {
/* 320 */     writeLongLE(paramArrayOfbyte, paramInt, Double.doubleToRawLongBits(paramDouble));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h\\util\Bits.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */