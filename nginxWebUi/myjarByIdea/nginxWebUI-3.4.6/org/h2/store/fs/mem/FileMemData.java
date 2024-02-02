package org.h2.store.fs.mem;

import java.io.IOException;
import java.nio.channels.NonWritableChannelException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.h2.compress.CompressLZF;
import org.h2.util.MathUtils;

class FileMemData {
   private static final int CACHE_SIZE = 8;
   private static final int BLOCK_SIZE_SHIFT = 10;
   private static final int BLOCK_SIZE = 1024;
   private static final int BLOCK_SIZE_MASK = 1023;
   private static final CompressLZF LZF = new CompressLZF();
   private static final byte[] BUFFER = new byte[2048];
   private static final byte[] COMPRESSED_EMPTY_BLOCK;
   private static final Cache<CompressItem, CompressItem> COMPRESS_LATER = new Cache(8);
   private String name;
   private final int id;
   private final boolean compress;
   private volatile long length;
   private AtomicReference<byte[]>[] data;
   private long lastModified;
   private boolean isReadOnly;
   private boolean isLockedExclusive;
   private int sharedLockCount;

   FileMemData(String var1, boolean var2) {
      this.name = var1;
      this.id = var1.hashCode();
      this.compress = var2;
      this.data = new AtomicReference[0];
      this.lastModified = System.currentTimeMillis();
   }

   private byte[] getPage(int var1) {
      AtomicReference[] var2 = this.data;
      return var1 >= var2.length ? null : (byte[])var2[var1].get();
   }

   private void setPage(int var1, byte[] var2, byte[] var3, boolean var4) {
      AtomicReference[] var5 = this.data;
      if (var1 < var5.length) {
         if (var4) {
            var5[var1].set(var3);
         } else {
            var5[var1].compareAndSet(var2, var3);
         }

      }
   }

   int getId() {
      return this.id;
   }

   synchronized boolean lockExclusive() {
      if (this.sharedLockCount <= 0 && !this.isLockedExclusive) {
         this.isLockedExclusive = true;
         return true;
      } else {
         return false;
      }
   }

   synchronized boolean lockShared() {
      if (this.isLockedExclusive) {
         return false;
      } else {
         ++this.sharedLockCount;
         return true;
      }
   }

   synchronized void unlock() throws IOException {
      if (this.isLockedExclusive) {
         this.isLockedExclusive = false;
      } else {
         if (this.sharedLockCount <= 0) {
            throw new IOException("not locked");
         }

         --this.sharedLockCount;
      }

   }

   private void compressLater(int var1) {
      CompressItem var2 = new CompressItem();
      var2.file = this;
      var2.page = var1;
      synchronized(LZF) {
         COMPRESS_LATER.put(var2, var2);
      }
   }

   private byte[] expand(int var1) {
      byte[] var2 = this.getPage(var1);
      if (var2.length == 1024) {
         return var2;
      } else {
         byte[] var3 = new byte[1024];
         if (var2 != COMPRESSED_EMPTY_BLOCK) {
            synchronized(LZF) {
               LZF.expand(var2, 0, var2.length, var3, 0, 1024);
            }
         }

         this.setPage(var1, var2, var3, false);
         return var3;
      }
   }

   void compress(int var1) {
      byte[] var2 = this.getPage(var1);
      if (var2 != null && var2.length == 1024) {
         synchronized(LZF) {
            int var4 = LZF.compress(var2, 0, 1024, BUFFER, 0);
            if (var4 <= 1024) {
               byte[] var5 = Arrays.copyOf(BUFFER, var4);
               this.setPage(var1, var2, var5, false);
            }

         }
      }
   }

   void touch(boolean var1) {
      if (!this.isReadOnly && !var1) {
         this.lastModified = System.currentTimeMillis();
      } else {
         throw new NonWritableChannelException();
      }
   }

   long length() {
      return this.length;
   }

   void truncate(long var1) {
      this.changeLength(var1);
      long var3 = MathUtils.roundUpLong(var1, 1024L);
      if (var3 != var1) {
         int var5 = (int)(var1 >>> 10);
         byte[] var6 = this.expand(var5);
         byte[] var7 = Arrays.copyOf(var6, var6.length);

         for(int var8 = (int)(var1 & 1023L); var8 < 1024; ++var8) {
            var7[var8] = 0;
         }

         this.setPage(var5, var6, var7, true);
         if (this.compress) {
            this.compressLater(var5);
         }
      }

   }

   private void changeLength(long var1) {
      this.length = var1;
      var1 = MathUtils.roundUpLong(var1, 1024L);
      int var3 = (int)(var1 >>> 10);
      if (var3 != this.data.length) {
         AtomicReference[] var4 = (AtomicReference[])Arrays.copyOf(this.data, var3);

         for(int var5 = this.data.length; var5 < var3; ++var5) {
            var4[var5] = new AtomicReference(COMPRESSED_EMPTY_BLOCK);
         }

         this.data = var4;
      }

   }

   long readWrite(long var1, byte[] var3, int var4, int var5, boolean var6) {
      long var7 = var1 + (long)var5;
      if (var7 > this.length) {
         if (var6) {
            this.changeLength(var7);
         } else {
            var5 = (int)(this.length - var1);
         }
      }

      while(var5 > 0) {
         int var9 = (int)Math.min((long)var5, 1024L - (var1 & 1023L));
         int var10 = (int)(var1 >>> 10);
         byte[] var11 = this.expand(var10);
         int var12 = (int)(var1 & 1023L);
         if (var6) {
            byte[] var13 = Arrays.copyOf(var11, var11.length);
            System.arraycopy(var3, var4, var13, var12, var9);
            this.setPage(var10, var11, var13, true);
         } else {
            System.arraycopy(var11, var12, var3, var4, var9);
         }

         if (this.compress) {
            this.compressLater(var10);
         }

         var4 += var9;
         var1 += (long)var9;
         var5 -= var9;
      }

      return var1;
   }

   void setName(String var1) {
      this.name = var1;
   }

   String getName() {
      return this.name;
   }

   long getLastModified() {
      return this.lastModified;
   }

   boolean canWrite() {
      return !this.isReadOnly;
   }

   boolean setReadOnly() {
      this.isReadOnly = true;
      return true;
   }

   static {
      byte[] var0 = new byte[1024];
      int var1 = LZF.compress(var0, 0, 1024, BUFFER, 0);
      COMPRESSED_EMPTY_BLOCK = Arrays.copyOf(BUFFER, var1);
   }

   static class CompressItem {
      FileMemData file;
      int page;

      public int hashCode() {
         return this.page ^ this.file.getId();
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof CompressItem)) {
            return false;
         } else {
            CompressItem var2 = (CompressItem)var1;
            return var2.page == this.page && var2.file == this.file;
         }
      }
   }

   static class Cache<K, V> extends LinkedHashMap<K, V> {
      private static final long serialVersionUID = 1L;
      private final int size;

      Cache(int var1) {
         super(var1, 0.75F, true);
         this.size = var1;
      }

      public synchronized V put(K var1, V var2) {
         return super.put(var1, var2);
      }

      protected boolean removeEldestEntry(Map.Entry<K, V> var1) {
         if (this.size() < this.size) {
            return false;
         } else {
            CompressItem var2 = (CompressItem)var1.getKey();
            var2.file.compress(var2.page);
            return true;
         }
      }
   }
}
