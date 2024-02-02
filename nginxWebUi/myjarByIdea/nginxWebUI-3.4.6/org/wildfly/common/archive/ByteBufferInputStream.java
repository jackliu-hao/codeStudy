package org.wildfly.common.archive;

import java.io.InputStream;
import java.nio.ByteBuffer;

final class ByteBufferInputStream extends InputStream {
   private final ByteBuffer[] bufs;
   private final long offset;
   private final long size;
   long pos;
   long mark;

   ByteBufferInputStream(ByteBuffer[] bufs, long offset, long size) {
      this.bufs = bufs;
      this.offset = offset;
      this.size = size;
   }

   public int read() {
      return this.pos < this.size ? Archive.getByte(this.bufs, this.offset + this.pos++) : -1;
   }

   public int read(byte[] b) {
      return this.read(b, 0, b.length);
   }

   public int read(byte[] b, int off, int len) {
      long rem = this.size - this.pos;
      if (rem == 0L) {
         return -1;
      } else {
         int realLen = (int)Math.min((long)len, rem);
         if (realLen > 0) {
            Archive.readBytes(this.bufs, this.offset + this.pos, b, off, realLen);
            return realLen;
         } else {
            return 0;
         }
      }
   }

   public long skip(long n) {
      long rem = this.size - this.pos;
      long cnt = Math.min(rem, n);
      if (cnt > 0L) {
         this.pos += cnt;
         return cnt;
      } else {
         return 0L;
      }
   }

   public int available() {
      return (int)Math.min(2147483647L, this.size - this.pos);
   }

   public void close() {
   }

   public void mark(int readLimit) {
      this.mark = this.pos;
   }

   public void reset() {
      this.pos = this.mark;
   }

   public boolean markSupported() {
      return true;
   }
}
