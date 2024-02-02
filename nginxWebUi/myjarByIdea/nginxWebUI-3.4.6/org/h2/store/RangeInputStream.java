package org.h2.store;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.h2.util.IOUtils;

public final class RangeInputStream extends FilterInputStream {
   private long limit;

   public RangeInputStream(InputStream var1, long var2, long var4) throws IOException {
      super(var1);
      this.limit = var4;
      IOUtils.skipFully(var1, var2);
   }

   public int read() throws IOException {
      if (this.limit <= 0L) {
         return -1;
      } else {
         int var1 = this.in.read();
         if (var1 >= 0) {
            --this.limit;
         }

         return var1;
      }
   }

   public int read(byte[] var1, int var2, int var3) throws IOException {
      if (this.limit <= 0L) {
         return -1;
      } else {
         if ((long)var3 > this.limit) {
            var3 = (int)this.limit;
         }

         int var4 = this.in.read(var1, var2, var3);
         if (var4 > 0) {
            this.limit -= (long)var4;
         }

         return var4;
      }
   }

   public long skip(long var1) throws IOException {
      if (var1 > this.limit) {
         var1 = (long)((int)this.limit);
      }

      var1 = this.in.skip(var1);
      this.limit -= var1;
      return var1;
   }

   public int available() throws IOException {
      int var1 = this.in.available();
      return (long)var1 > this.limit ? (int)this.limit : var1;
   }

   public void close() throws IOException {
      this.in.close();
   }

   public void mark(int var1) {
   }

   public synchronized void reset() throws IOException {
      throw new IOException("mark/reset not supported");
   }

   public boolean markSupported() {
      return false;
   }
}
