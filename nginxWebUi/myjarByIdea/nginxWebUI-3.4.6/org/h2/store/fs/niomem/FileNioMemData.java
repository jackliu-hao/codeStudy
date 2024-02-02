package org.h2.store.fs.niomem;

import java.nio.ByteBuffer;
import java.nio.channels.NonWritableChannelException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.h2.compress.CompressLZF;
import org.h2.util.MathUtils;

class FileNioMemData {
   private static final int CACHE_MIN_SIZE = 8;
   private static final int BLOCK_SIZE_SHIFT = 16;
   private static final int BLOCK_SIZE = 65536;
   private static final int BLOCK_SIZE_MASK = 65535;
   private static final ByteBuffer COMPRESSED_EMPTY_BLOCK;
   private static final ThreadLocal<CompressLZF> LZF_THREAD_LOCAL = ThreadLocal.withInitial(CompressLZF::new);
   private static final ThreadLocal<byte[]> COMPRESS_OUT_BUF_THREAD_LOCAL = ThreadLocal.withInitial(() -> {
      return new byte[131072];
   });
   final int nameHashCode;
   private final CompressLaterCache<CompressItem, CompressItem> compressLaterCache = new CompressLaterCache(8);
   private String name;
   private final boolean compress;
   private final float compressLaterCachePercent;
   private volatile long length;
   private AtomicReference<ByteBuffer>[] buffers;
   private long lastModified;
   private boolean isReadOnly;
   private boolean isLockedExclusive;
   private int sharedLockCount;
   private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

   FileNioMemData(String var1, boolean var2, float var3) {
      this.name = var1;
      this.nameHashCode = var1.hashCode();
      this.compress = var2;
      this.compressLaterCachePercent = var3;
      this.buffers = new AtomicReference[0];
      this.lastModified = System.currentTimeMillis();
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

   synchronized void unlock() {
      if (this.isLockedExclusive) {
         this.isLockedExclusive = false;
      } else {
         this.sharedLockCount = Math.max(0, this.sharedLockCount - 1);
      }

   }

   private void addToCompressLaterCache(int var1) {
      CompressItem var2 = new CompressItem(this, var1);
      this.compressLaterCache.put(var2, var2);
   }

   private ByteBuffer expandPage(int var1) {
      ByteBuffer var2 = (ByteBuffer)this.buffers[var1].get();
      if (var2.capacity() == 65536) {
         return var2;
      } else {
         synchronized(var2) {
            if (var2.capacity() == 65536) {
               return var2;
            } else {
               ByteBuffer var4 = ByteBuffer.allocateDirect(65536);
               if (var2 != COMPRESSED_EMPTY_BLOCK) {
                  var2.position(0);
                  CompressLZF.expand(var2, var4);
               }

               this.buffers[var1].compareAndSet(var2, var4);
               return var4;
            }
         }
      }
   }

   void compressPage(int var1) {
      ByteBuffer var2 = (ByteBuffer)this.buffers[var1].get();
      synchronized(var2) {
         if (var2.capacity() == 65536) {
            byte[] var4 = (byte[])COMPRESS_OUT_BUF_THREAD_LOCAL.get();
            int var5 = ((CompressLZF)LZF_THREAD_LOCAL.get()).compress(var2, 0, var4, 0);
            ByteBuffer var6 = ByteBuffer.allocateDirect(var5);
            var6.put(var4, 0, var5);
            this.buffers[var1].compareAndSet(var2, var6);
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
      this.rwLock.writeLock().lock();

      try {
         this.changeLength(var1);
         long var3 = MathUtils.roundUpLong(var1, 65536L);
         if (var3 != var1) {
            int var5 = (int)(var1 >>> 16);
            ByteBuffer var6 = this.expandPage(var5);

            for(int var7 = (int)(var1 & 65535L); var7 < 65536; ++var7) {
               var6.put(var7, (byte)0);
            }

            if (this.compress) {
               this.addToCompressLaterCache(var5);
            }
         }
      } finally {
         this.rwLock.writeLock().unlock();
      }

   }

   private void changeLength(long var1) {
      this.length = var1;
      var1 = MathUtils.roundUpLong(var1, 65536L);
      int var3 = (int)(var1 >>> 16);
      if (var3 != this.buffers.length) {
         AtomicReference[] var4 = new AtomicReference[var3];
         System.arraycopy(this.buffers, 0, var4, 0, Math.min(this.buffers.length, var4.length));

         for(int var5 = this.buffers.length; var5 < var3; ++var5) {
            var4[var5] = new AtomicReference(COMPRESSED_EMPTY_BLOCK);
         }

         this.buffers = var4;
      }

      this.compressLaterCache.setCacheSize(Math.max(8, (int)((float)var3 * this.compressLaterCachePercent / 100.0F)));
   }

   long readWrite(long var1, ByteBuffer var3, int var4, int var5, boolean var6) {
      Object var7 = var6 ? this.rwLock.writeLock() : this.rwLock.readLock();
      ((Lock)var7).lock();

      long var19;
      try {
         long var8 = var1 + (long)var5;
         if (var8 > this.length) {
            if (var6) {
               this.changeLength(var8);
            } else {
               var5 = (int)(this.length - var1);
            }
         }

         while(var5 > 0) {
            int var10 = (int)Math.min((long)var5, 65536L - (var1 & 65535L));
            int var11 = (int)(var1 >>> 16);
            ByteBuffer var12 = this.expandPage(var11);
            int var13 = (int)(var1 & 65535L);
            ByteBuffer var14;
            if (var6) {
               var14 = var3.slice();
               ByteBuffer var15 = var12.duplicate();
               var14.position(var4);
               var14.limit(var4 + var10);
               var15.position(var13);
               var15.put(var14);
            } else {
               var14 = var12.duplicate();
               var14.position(var13);
               var14.limit(var10 + var13);
               int var20 = var3.position();
               var3.position(var4);
               var3.put(var14);
               var3.position(var20);
            }

            if (this.compress) {
               this.addToCompressLaterCache(var11);
            }

            var4 += var10;
            var1 += (long)var10;
            var5 -= var10;
         }

         var19 = var1;
      } finally {
         ((Lock)var7).unlock();
      }

      return var19;
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
      byte[] var0 = new byte[65536];
      byte[] var1 = new byte[131072];
      int var2 = (new CompressLZF()).compress(var0, 0, 65536, var1, 0);
      COMPRESSED_EMPTY_BLOCK = ByteBuffer.allocateDirect(var2);
      COMPRESSED_EMPTY_BLOCK.put(var1, 0, var2);
   }

   static class CompressItem {
      public final FileNioMemData data;
      public final int page;

      public CompressItem(FileNioMemData var1, int var2) {
         this.data = var1;
         this.page = var2;
      }

      public int hashCode() {
         return this.page ^ this.data.nameHashCode;
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof CompressItem)) {
            return false;
         } else {
            CompressItem var2 = (CompressItem)var1;
            return var2.data == this.data && var2.page == this.page;
         }
      }
   }

   static class CompressLaterCache<K, V> extends LinkedHashMap<K, V> {
      private static final long serialVersionUID = 1L;
      private int size;

      CompressLaterCache(int var1) {
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
            var2.data.compressPage(var2.page);
            return true;
         }
      }

      public void setCacheSize(int var1) {
         this.size = var1;
      }
   }
}
