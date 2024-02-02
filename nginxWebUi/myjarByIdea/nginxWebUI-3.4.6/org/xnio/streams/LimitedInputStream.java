package org.xnio.streams;

import java.io.IOException;
import java.io.InputStream;
import org.xnio._private.Messages;

public final class LimitedInputStream extends InputStream {
   private final InputStream delegate;
   private long remaining;
   private long mark = -1L;

   public LimitedInputStream(InputStream delegate, long size) {
      this.delegate = delegate;
      this.remaining = size;
   }

   public int read() throws IOException {
      long remaining = this.remaining;
      if (remaining > 0L) {
         int b = this.delegate.read();
         if (b >= 0) {
            this.remaining = remaining - 1L;
         }

         return b;
      } else {
         return -1;
      }
   }

   public int read(byte[] b, int off, int len) throws IOException {
      long remaining = this.remaining;
      if (remaining == 0L) {
         return -1;
      } else {
         int cnt = this.delegate.read(b, off, (int)Math.min((long)len, remaining));
         if (cnt == -1) {
            return -1;
         } else {
            this.remaining = remaining - (long)cnt;
            return cnt;
         }
      }
   }

   public long skip(long n) throws IOException {
      long remaining = this.remaining;
      if (remaining != 0L && n > 0L) {
         long cnt = this.delegate.skip(Math.min(n, remaining));
         if (cnt > 0L) {
            this.remaining = remaining - cnt;
         }

         return cnt;
      } else {
         return 0L;
      }
   }

   public int available() throws IOException {
      return Math.min(this.delegate.available(), (int)Math.min(2147483647L, this.remaining));
   }

   public void close() throws IOException {
      this.remaining = 0L;
      this.delegate.close();
   }

   public void mark(int limit) {
      if (this.markSupported()) {
         this.delegate.mark(limit);
         this.mark = this.remaining;
      }

   }

   public void reset() throws IOException {
      long mark = this.mark;
      if (mark == -1L) {
         throw Messages.msg.markNotSet();
      } else {
         this.delegate.reset();
         this.remaining = mark;
      }
   }

   public boolean markSupported() {
      return this.delegate.markSupported();
   }
}
