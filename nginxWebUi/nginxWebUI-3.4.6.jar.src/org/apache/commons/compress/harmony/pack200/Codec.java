/*     */ package org.apache.commons.compress.harmony.pack200;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
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
/*     */ public abstract class Codec
/*     */ {
/*  36 */   public static final BHSDCodec BCI5 = new BHSDCodec(5, 4);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  41 */   public static final BHSDCodec BRANCH5 = new BHSDCodec(5, 4, 2);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  46 */   public static final BHSDCodec BYTE1 = new BHSDCodec(1, 256);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  52 */   public static final BHSDCodec CHAR3 = new BHSDCodec(3, 128);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  58 */   public static final BHSDCodec DELTA5 = new BHSDCodec(5, 64, 1, 1);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  64 */   public static final BHSDCodec MDELTA5 = new BHSDCodec(5, 64, 2, 1);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  69 */   public static final BHSDCodec SIGNED5 = new BHSDCodec(5, 64, 1);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  75 */   public static final BHSDCodec UDELTA5 = new BHSDCodec(5, 64, 0, 1);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  80 */   public static final BHSDCodec UNSIGNED5 = new BHSDCodec(5, 64);
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
/*     */   public int lastBandLength;
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
/*     */   public abstract int decode(InputStream paramInputStream) throws IOException, Pack200Exception;
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
/*     */   public abstract byte[] encode(int paramInt1, int paramInt2) throws Pack200Exception;
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
/*     */   public abstract byte[] encode(int paramInt) throws Pack200Exception;
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
/*     */   public abstract int decode(InputStream paramInputStream, long paramLong) throws IOException, Pack200Exception;
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
/*     */   public int[] decodeInts(int n, InputStream in) throws IOException, Pack200Exception {
/* 147 */     this.lastBandLength = 0;
/* 148 */     int[] result = new int[n];
/* 149 */     int last = 0;
/* 150 */     for (int i = 0; i < n; i++) {
/* 151 */       result[i] = last = decode(in, last);
/*     */     }
/* 153 */     return result;
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
/*     */   public int[] decodeInts(int n, InputStream in, int firstValue) throws IOException, Pack200Exception {
/* 169 */     int[] result = new int[n + 1];
/* 170 */     result[0] = firstValue;
/* 171 */     int last = firstValue;
/* 172 */     for (int i = 1; i < n + 1; i++) {
/* 173 */       result[i] = last = decode(in, last);
/*     */     }
/* 175 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] encode(int[] ints) throws Pack200Exception {
/* 186 */     int total = 0;
/* 187 */     byte[][] bytes = new byte[ints.length][];
/* 188 */     for (int i = 0; i < ints.length; i++) {
/* 189 */       bytes[i] = encode(ints[i], (i > 0) ? ints[i - 1] : 0);
/* 190 */       total += (bytes[i]).length;
/*     */     } 
/* 192 */     byte[] encoded = new byte[total];
/* 193 */     int index = 0;
/* 194 */     for (int j = 0; j < bytes.length; j++) {
/* 195 */       System.arraycopy(bytes[j], 0, encoded, index, (bytes[j]).length);
/* 196 */       index += (bytes[j]).length;
/*     */     } 
/* 198 */     return encoded;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmony\pack200\Codec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */