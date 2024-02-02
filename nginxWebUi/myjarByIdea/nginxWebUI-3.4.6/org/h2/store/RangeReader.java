package org.h2.store;

import java.io.IOException;
import java.io.Reader;
import org.h2.util.IOUtils;

public final class RangeReader extends Reader {
   private final Reader r;
   private long limit;

   public RangeReader(Reader var1, long var2, long var4) throws IOException {
      this.r = var1;
      this.limit = var4;
      IOUtils.skipFully(var1, var2);
   }

   public int read() throws IOException {
      if (this.limit <= 0L) {
         return -1;
      } else {
         int var1 = this.r.read();
         if (var1 >= 0) {
            --this.limit;
         }

         return var1;
      }
   }

   public int read(char[] var1, int var2, int var3) throws IOException {
      if (this.limit <= 0L) {
         return -1;
      } else {
         if ((long)var3 > this.limit) {
            var3 = (int)this.limit;
         }

         int var4 = this.r.read(var1, var2, var3);
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

      var1 = this.r.skip(var1);
      this.limit -= var1;
      return var1;
   }

   public boolean ready() throws IOException {
      return this.limit > 0L ? this.r.ready() : false;
   }

   public boolean markSupported() {
      return false;
   }

   public void mark(int var1) throws IOException {
      throw new IOException("mark() not supported");
   }

   public void reset() throws IOException {
      throw new IOException("reset() not supported");
   }

   public void close() throws IOException {
      this.r.close();
   }
}
