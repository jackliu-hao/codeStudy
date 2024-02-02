package org.h2.mvstore.cache;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import org.h2.store.fs.FileBase;
import org.h2.store.fs.FilePath;
import org.h2.store.fs.FilePathWrapper;

public class FilePathCache extends FilePathWrapper {
   public static final FilePathCache INSTANCE = new FilePathCache();

   public static FileChannel wrap(FileChannel var0) {
      return new FileCache(var0);
   }

   public FileChannel open(String var1) throws IOException {
      return new FileCache(this.getBase().open(var1));
   }

   public String getScheme() {
      return "cache";
   }

   static {
      FilePath.register(INSTANCE);
   }

   public static class FileCache extends FileBase {
      private static final int CACHE_BLOCK_SIZE = 4096;
      private final FileChannel base;
      private final CacheLongKeyLIRS<ByteBuffer> cache;

      FileCache(FileChannel var1) {
         CacheLongKeyLIRS.Config var2 = new CacheLongKeyLIRS.Config();
         var2.maxMemory = 1048576L;
         this.cache = new CacheLongKeyLIRS(var2);
         this.base = var1;
      }

      protected void implCloseChannel() throws IOException {
         this.base.close();
      }

      public FileChannel position(long var1) throws IOException {
         this.base.position(var1);
         return this;
      }

      public long position() throws IOException {
         return this.base.position();
      }

      public int read(ByteBuffer var1) throws IOException {
         return this.base.read(var1);
      }

      public synchronized int read(ByteBuffer var1, long var2) throws IOException {
         long var4 = getCachePos(var2);
         int var6 = (int)(var2 - var4);
         int var7 = 4096 - var6;
         var7 = Math.min(var7, var1.remaining());
         ByteBuffer var8 = (ByteBuffer)this.cache.get(var4);
         if (var8 == null) {
            var8 = ByteBuffer.allocate(4096);
            long var9 = var4;

            while(true) {
               int var11 = this.base.read(var8, var9);
               if (var11 <= 0 || var8.remaining() == 0) {
                  var11 = var8.position();
                  if (var11 == 4096) {
                     this.cache.put(var4, var8, 4096);
                  } else {
                     if (var11 <= 0) {
                        return -1;
                     }

                     var7 = Math.min(var7, var11 - var6);
                  }
                  break;
               }

               var9 += (long)var11;
            }
         }

         var1.put(var8.array(), var6, var7);
         return var7 == 0 ? -1 : var7;
      }

      private static long getCachePos(long var0) {
         return var0 / 4096L * 4096L;
      }

      public long size() throws IOException {
         return this.base.size();
      }

      public synchronized FileChannel truncate(long var1) throws IOException {
         this.cache.clear();
         this.base.truncate(var1);
         return this;
      }

      public synchronized int write(ByteBuffer var1, long var2) throws IOException {
         this.clearCache(var1, var2);
         return this.base.write(var1, var2);
      }

      public synchronized int write(ByteBuffer var1) throws IOException {
         this.clearCache(var1, this.position());
         return this.base.write(var1);
      }

      private void clearCache(ByteBuffer var1, long var2) {
         if (this.cache.size() > 0) {
            int var4 = var1.remaining();

            for(long var5 = getCachePos(var2); var4 > 0; var4 -= 4096) {
               this.cache.remove(var5);
               var5 += 4096L;
            }
         }

      }

      public void force(boolean var1) throws IOException {
         this.base.force(var1);
      }

      public FileLock tryLock(long var1, long var3, boolean var5) throws IOException {
         return this.base.tryLock(var1, var3, var5);
      }

      public String toString() {
         return "cache:" + this.base.toString();
      }
   }
}
