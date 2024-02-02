package org.h2.store.fs.split;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import org.h2.message.DbException;
import org.h2.mvstore.DataUtils;
import org.h2.store.fs.FileBaseDefault;
import org.h2.store.fs.FilePath;

class FileSplit extends FileBaseDefault {
   private final FilePathSplit filePath;
   private final String mode;
   private final long maxLength;
   private FileChannel[] list;
   private volatile long length;

   FileSplit(FilePathSplit var1, String var2, FileChannel[] var3, long var4, long var6) {
      this.filePath = var1;
      this.mode = var2;
      this.list = var3;
      this.length = var4;
      this.maxLength = var6;
   }

   public synchronized void implCloseChannel() throws IOException {
      FileChannel[] var1 = this.list;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         FileChannel var4 = var1[var3];
         var4.close();
      }

   }

   public long size() {
      return this.length;
   }

   public synchronized int read(ByteBuffer var1, long var2) throws IOException {
      int var4 = var1.remaining();
      if (var4 == 0) {
         return 0;
      } else {
         var4 = (int)Math.min((long)var4, this.length - var2);
         if (var4 <= 0) {
            return -1;
         } else {
            long var5 = var2 % this.maxLength;
            var4 = (int)Math.min((long)var4, this.maxLength - var5);
            FileChannel var7 = this.getFileChannel(var2);
            return var7.read(var1, var5);
         }
      }
   }

   private FileChannel getFileChannel(long var1) throws IOException {
      int var3;
      FileChannel[] var5;
      for(var3 = (int)(var1 / this.maxLength); var3 >= this.list.length; this.list = var5) {
         int var4 = this.list.length;
         var5 = new FileChannel[var4 + 1];
         System.arraycopy(this.list, 0, var5, 0, var4);
         FilePath var6 = this.filePath.getBase(var4);
         var5[var4] = var6.open(this.mode);
      }

      return this.list[var3];
   }

   protected void implTruncate(long var1) throws IOException {
      if (var1 < this.length) {
         int var3 = 1 + (int)(var1 / this.maxLength);
         if (var3 < this.list.length) {
            FileChannel[] var4 = new FileChannel[var3];

            for(int var5 = this.list.length - 1; var5 >= var3; --var5) {
               this.list[var5].truncate(0L);
               this.list[var5].close();

               try {
                  this.filePath.getBase(var5).delete();
               } catch (DbException var7) {
                  throw DataUtils.convertToIOException(var7);
               }
            }

            System.arraycopy(this.list, 0, var4, 0, var4.length);
            this.list = var4;
         }

         long var8 = var1 - this.maxLength * (long)(var3 - 1);
         this.list[this.list.length - 1].truncate(var8);
         this.length = var1;
      }
   }

   public synchronized void force(boolean var1) throws IOException {
      FileChannel[] var2 = this.list;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         FileChannel var5 = var2[var4];
         var5.force(var1);
      }

   }

   public synchronized int write(ByteBuffer var1, long var2) throws IOException {
      long var4;
      if (var2 >= this.length && var2 > this.maxLength) {
         var4 = var2;

         for(long var6 = this.length - this.length % this.maxLength + this.maxLength; var6 < var2; var6 += this.maxLength) {
            if (var6 > this.length) {
               this.position(var6 - 1L);
               this.write(ByteBuffer.wrap(new byte[1]));
            }

            var2 = var4;
         }
      }

      var4 = var2 % this.maxLength;
      int var10 = var1.remaining();
      FileChannel var7 = this.getFileChannel(var2);
      int var8 = (int)Math.min((long)var10, this.maxLength - var4);
      if (var8 == var10) {
         var8 = var7.write(var1, var4);
      } else {
         int var9 = var1.limit();
         var1.limit(var1.position() + var8);
         var8 = var7.write(var1, var4);
         var1.limit(var9);
      }

      this.length = Math.max(this.length, var2 + (long)var8);
      return var8;
   }

   public synchronized FileLock tryLock(long var1, long var3, boolean var5) throws IOException {
      return this.list[0].tryLock(var1, var3, var5);
   }

   public String toString() {
      return this.filePath.toString();
   }
}
