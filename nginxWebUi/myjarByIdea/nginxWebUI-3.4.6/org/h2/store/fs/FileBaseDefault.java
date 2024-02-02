package org.h2.store.fs;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public abstract class FileBaseDefault extends FileBase {
   private long position = 0L;

   public final synchronized long position() throws IOException {
      return this.position;
   }

   public final synchronized FileChannel position(long var1) throws IOException {
      if (var1 < 0L) {
         throw new IllegalArgumentException();
      } else {
         this.position = var1;
         return this;
      }
   }

   public final synchronized int read(ByteBuffer var1) throws IOException {
      int var2 = this.read(var1, this.position);
      if (var2 > 0) {
         this.position += (long)var2;
      }

      return var2;
   }

   public final synchronized int write(ByteBuffer var1) throws IOException {
      int var2 = this.write(var1, this.position);
      if (var2 > 0) {
         this.position += (long)var2;
      }

      return var2;
   }

   public final synchronized FileChannel truncate(long var1) throws IOException {
      this.implTruncate(var1);
      if (var1 < this.position) {
         this.position = var1;
      }

      return this;
   }

   protected abstract void implTruncate(long var1) throws IOException;
}
