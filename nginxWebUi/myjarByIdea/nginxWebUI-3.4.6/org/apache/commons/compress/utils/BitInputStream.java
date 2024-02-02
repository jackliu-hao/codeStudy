package org.apache.commons.compress.utils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;

public class BitInputStream implements Closeable {
   private static final int MAXIMUM_CACHE_SIZE = 63;
   private static final long[] MASKS = new long[64];
   private final CountingInputStream in;
   private final ByteOrder byteOrder;
   private long bitsCached;
   private int bitsCachedSize;

   public BitInputStream(InputStream in, ByteOrder byteOrder) {
      this.in = new CountingInputStream(in);
      this.byteOrder = byteOrder;
   }

   public void close() throws IOException {
      this.in.close();
   }

   public void clearBitCache() {
      this.bitsCached = 0L;
      this.bitsCachedSize = 0;
   }

   public long readBits(int count) throws IOException {
      if (count >= 0 && count <= 63) {
         if (this.ensureCache(count)) {
            return -1L;
         } else {
            return this.bitsCachedSize < count ? this.processBitsGreater57(count) : this.readCachedBits(count);
         }
      } else {
         throw new IOException("count must not be negative or greater than 63");
      }
   }

   public int bitsCached() {
      return this.bitsCachedSize;
   }

   public long bitsAvailable() throws IOException {
      return (long)this.bitsCachedSize + 8L * (long)this.in.available();
   }

   public void alignWithByteBoundary() {
      int toSkip = this.bitsCachedSize % 8;
      if (toSkip > 0) {
         this.readCachedBits(toSkip);
      }

   }

   public long getBytesRead() {
      return this.in.getBytesRead();
   }

   private long processBitsGreater57(int count) throws IOException {
      long overflow = 0L;
      int bitsToAddCount = count - this.bitsCachedSize;
      int overflowBits = 8 - bitsToAddCount;
      long nextByte = (long)this.in.read();
      if (nextByte < 0L) {
         return nextByte;
      } else {
         long bitsToAdd;
         if (this.byteOrder == ByteOrder.LITTLE_ENDIAN) {
            bitsToAdd = nextByte & MASKS[bitsToAddCount];
            this.bitsCached |= bitsToAdd << this.bitsCachedSize;
            overflow = nextByte >>> bitsToAddCount & MASKS[overflowBits];
         } else {
            this.bitsCached <<= bitsToAddCount;
            bitsToAdd = nextByte >>> overflowBits & MASKS[bitsToAddCount];
            this.bitsCached |= bitsToAdd;
            overflow = nextByte & MASKS[overflowBits];
         }

         long bitsOut = this.bitsCached & MASKS[count];
         this.bitsCached = overflow;
         this.bitsCachedSize = overflowBits;
         return bitsOut;
      }
   }

   private long readCachedBits(int count) {
      long bitsOut;
      if (this.byteOrder == ByteOrder.LITTLE_ENDIAN) {
         bitsOut = this.bitsCached & MASKS[count];
         this.bitsCached >>>= count;
      } else {
         bitsOut = this.bitsCached >> this.bitsCachedSize - count & MASKS[count];
      }

      this.bitsCachedSize -= count;
      return bitsOut;
   }

   private boolean ensureCache(int count) throws IOException {
      for(; this.bitsCachedSize < count && this.bitsCachedSize < 57; this.bitsCachedSize += 8) {
         long nextByte = (long)this.in.read();
         if (nextByte < 0L) {
            return true;
         }

         if (this.byteOrder == ByteOrder.LITTLE_ENDIAN) {
            this.bitsCached |= nextByte << this.bitsCachedSize;
         } else {
            this.bitsCached <<= 8;
            this.bitsCached |= nextByte;
         }
      }

      return false;
   }

   static {
      for(int i = 1; i <= 63; ++i) {
         MASKS[i] = (MASKS[i - 1] << 1) + 1L;
      }

   }
}
