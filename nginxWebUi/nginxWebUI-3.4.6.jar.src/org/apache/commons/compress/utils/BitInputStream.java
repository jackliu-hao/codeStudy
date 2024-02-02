/*     */ package org.apache.commons.compress.utils;
/*     */ 
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.nio.ByteOrder;
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
/*     */ public class BitInputStream
/*     */   implements Closeable
/*     */ {
/*     */   private static final int MAXIMUM_CACHE_SIZE = 63;
/*  33 */   private static final long[] MASKS = new long[64];
/*     */   
/*     */   static {
/*  36 */     for (int i = 1; i <= 63; i++) {
/*  37 */       MASKS[i] = (MASKS[i - 1] << 1L) + 1L;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private final CountingInputStream in;
/*     */   
/*     */   private final ByteOrder byteOrder;
/*     */   
/*     */   private long bitsCached;
/*     */   
/*     */   private int bitsCachedSize;
/*     */ 
/*     */   
/*     */   public BitInputStream(InputStream in, ByteOrder byteOrder) {
/*  53 */     this.in = new CountingInputStream(in);
/*  54 */     this.byteOrder = byteOrder;
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/*  59 */     this.in.close();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clearBitCache() {
/*  67 */     this.bitsCached = 0L;
/*  68 */     this.bitsCachedSize = 0;
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
/*     */   public long readBits(int count) throws IOException {
/*  82 */     if (count < 0 || count > 63) {
/*  83 */       throw new IOException("count must not be negative or greater than 63");
/*     */     }
/*  85 */     if (ensureCache(count)) {
/*  86 */       return -1L;
/*     */     }
/*     */     
/*  89 */     if (this.bitsCachedSize < count) {
/*  90 */       return processBitsGreater57(count);
/*     */     }
/*  92 */     return readCachedBits(count);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int bitsCached() {
/* 102 */     return this.bitsCachedSize;
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
/*     */   public long bitsAvailable() throws IOException {
/* 114 */     return this.bitsCachedSize + 8L * this.in.available();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void alignWithByteBoundary() {
/* 122 */     int toSkip = this.bitsCachedSize % 8;
/* 123 */     if (toSkip > 0) {
/* 124 */       readCachedBits(toSkip);
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
/*     */   public long getBytesRead() {
/* 137 */     return this.in.getBytesRead();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private long processBitsGreater57(int count) throws IOException {
/* 143 */     long overflow = 0L;
/*     */ 
/*     */     
/* 146 */     int bitsToAddCount = count - this.bitsCachedSize;
/* 147 */     int overflowBits = 8 - bitsToAddCount;
/* 148 */     long nextByte = this.in.read();
/* 149 */     if (nextByte < 0L) {
/* 150 */       return nextByte;
/*     */     }
/* 152 */     if (this.byteOrder == ByteOrder.LITTLE_ENDIAN) {
/* 153 */       long bitsToAdd = nextByte & MASKS[bitsToAddCount];
/* 154 */       this.bitsCached |= bitsToAdd << this.bitsCachedSize;
/* 155 */       overflow = nextByte >>> bitsToAddCount & MASKS[overflowBits];
/*     */     } else {
/* 157 */       this.bitsCached <<= bitsToAddCount;
/* 158 */       long bitsToAdd = nextByte >>> overflowBits & MASKS[bitsToAddCount];
/* 159 */       this.bitsCached |= bitsToAdd;
/* 160 */       overflow = nextByte & MASKS[overflowBits];
/*     */     } 
/* 162 */     long bitsOut = this.bitsCached & MASKS[count];
/* 163 */     this.bitsCached = overflow;
/* 164 */     this.bitsCachedSize = overflowBits;
/* 165 */     return bitsOut;
/*     */   }
/*     */   
/*     */   private long readCachedBits(int count) {
/*     */     long bitsOut;
/* 170 */     if (this.byteOrder == ByteOrder.LITTLE_ENDIAN) {
/* 171 */       bitsOut = this.bitsCached & MASKS[count];
/* 172 */       this.bitsCached >>>= count;
/*     */     } else {
/* 174 */       bitsOut = this.bitsCached >> this.bitsCachedSize - count & MASKS[count];
/*     */     } 
/* 176 */     this.bitsCachedSize -= count;
/* 177 */     return bitsOut;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean ensureCache(int count) throws IOException {
/* 187 */     while (this.bitsCachedSize < count && this.bitsCachedSize < 57) {
/* 188 */       long nextByte = this.in.read();
/* 189 */       if (nextByte < 0L) {
/* 190 */         return true;
/*     */       }
/* 192 */       if (this.byteOrder == ByteOrder.LITTLE_ENDIAN) {
/* 193 */         this.bitsCached |= nextByte << this.bitsCachedSize;
/*     */       } else {
/* 195 */         this.bitsCached <<= 8L;
/* 196 */         this.bitsCached |= nextByte;
/*     */       } 
/* 198 */       this.bitsCachedSize += 8;
/*     */     } 
/* 200 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compres\\utils\BitInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */