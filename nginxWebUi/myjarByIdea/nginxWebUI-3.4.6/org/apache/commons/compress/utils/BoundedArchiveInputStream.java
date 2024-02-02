package org.apache.commons.compress.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public abstract class BoundedArchiveInputStream extends InputStream {
   private final long end;
   private ByteBuffer singleByteBuffer;
   private long loc;

   public BoundedArchiveInputStream(long start, long remaining) {
      this.end = start + remaining;
      if (this.end < start) {
         throw new IllegalArgumentException("Invalid length of stream at offset=" + start + ", length=" + remaining);
      } else {
         this.loc = start;
      }
   }

   public synchronized int read() throws IOException {
      if (this.loc >= this.end) {
         return -1;
      } else {
         if (this.singleByteBuffer == null) {
            this.singleByteBuffer = ByteBuffer.allocate(1);
         } else {
            this.singleByteBuffer.rewind();
         }

         int read = this.read(this.loc, this.singleByteBuffer);
         if (read < 1) {
            return -1;
         } else {
            ++this.loc;
            return this.singleByteBuffer.get() & 255;
         }
      }
   }

   public synchronized int read(byte[] b, int off, int len) throws IOException {
      if (this.loc >= this.end) {
         return -1;
      } else {
         long maxLen = Math.min((long)len, this.end - this.loc);
         if (maxLen <= 0L) {
            return 0;
         } else if (off >= 0 && off <= b.length && maxLen <= (long)(b.length - off)) {
            ByteBuffer buf = ByteBuffer.wrap(b, off, (int)maxLen);
            int ret = this.read(this.loc, buf);
            if (ret > 0) {
               this.loc += (long)ret;
            }

            return ret;
         } else {
            throw new IndexOutOfBoundsException("offset or len are out of bounds");
         }
      }
   }

   protected abstract int read(long var1, ByteBuffer var3) throws IOException;
}
