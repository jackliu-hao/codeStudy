package org.h2.mvstore;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.concurrent.atomic.AtomicLong;
import org.h2.mvstore.cache.FilePathCache;
import org.h2.store.fs.FilePath;
import org.h2.store.fs.encrypt.FileEncrypt;
import org.h2.store.fs.encrypt.FilePathEncrypt;

public class FileStore {
   protected final AtomicLong readCount = new AtomicLong();
   protected final AtomicLong readBytes = new AtomicLong();
   protected final AtomicLong writeCount = new AtomicLong();
   protected final AtomicLong writeBytes = new AtomicLong();
   protected final FreeSpaceBitSet freeSpace = new FreeSpaceBitSet(2, 4096);
   private String fileName;
   private boolean readOnly;
   protected long fileSize;
   private FileChannel file;
   private FileChannel encryptedFile;
   private FileLock fileLock;

   public String toString() {
      return this.fileName;
   }

   public ByteBuffer readFully(long var1, int var3) {
      ByteBuffer var4 = ByteBuffer.allocate(var3);
      DataUtils.readFully(this.file, var1, var4);
      this.readCount.incrementAndGet();
      this.readBytes.addAndGet((long)var3);
      return var4;
   }

   public void writeFully(long var1, ByteBuffer var3) {
      int var4 = var3.remaining();
      this.fileSize = Math.max(this.fileSize, var1 + (long)var4);
      DataUtils.writeFully(this.file, var1, var3);
      this.writeCount.incrementAndGet();
      this.writeBytes.addAndGet((long)var4);
   }

   public void open(String var1, boolean var2, char[] var3) {
      if (this.file == null) {
         FilePathCache.INSTANCE.getScheme();
         this.fileName = var1;
         FilePath var4 = FilePath.get(var1);
         FilePath var5 = var4.getParent();
         if (var5 != null && !var5.exists()) {
            throw DataUtils.newIllegalArgumentException("Directory does not exist: {0}", var5);
         } else {
            if (var4.exists() && !var4.canWrite()) {
               var2 = true;
            }

            this.readOnly = var2;

            try {
               this.file = var4.open(var2 ? "r" : "rw");
               if (var3 != null) {
                  byte[] var6 = FilePathEncrypt.getPasswordBytes(var3);
                  this.encryptedFile = this.file;
                  this.file = new FileEncrypt(var1, var6, this.file);
               }

               try {
                  if (var2) {
                     this.fileLock = this.file.tryLock(0L, Long.MAX_VALUE, true);
                  } else {
                     this.fileLock = this.file.tryLock();
                  }
               } catch (OverlappingFileLockException var10) {
                  throw DataUtils.newMVStoreException(7, "The file is locked: {0}", var1, var10);
               }

               if (this.fileLock == null) {
                  try {
                     this.close();
                  } catch (Exception var9) {
                  }

                  throw DataUtils.newMVStoreException(7, "The file is locked: {0}", var1);
               } else {
                  this.fileSize = this.file.size();
               }
            } catch (IOException var11) {
               try {
                  this.close();
               } catch (Exception var8) {
               }

               throw DataUtils.newMVStoreException(1, "Could not open file {0}", var1, var11);
            }
         }
      }
   }

   public void close() {
      try {
         if (this.file != null && this.file.isOpen()) {
            if (this.fileLock != null) {
               this.fileLock.release();
            }

            this.file.close();
         }
      } catch (Exception var5) {
         throw DataUtils.newMVStoreException(2, "Closing failed for file {0}", this.fileName, var5);
      } finally {
         this.fileLock = null;
         this.file = null;
      }

   }

   public void sync() {
      if (this.file != null) {
         try {
            this.file.force(true);
         } catch (IOException var2) {
            throw DataUtils.newMVStoreException(2, "Could not sync file {0}", this.fileName, var2);
         }
      }

   }

   public long size() {
      return this.fileSize;
   }

   public void truncate(long var1) {
      int var3 = 0;

      while(true) {
         try {
            this.writeCount.incrementAndGet();
            this.file.truncate(var1);
            this.fileSize = Math.min(this.fileSize, var1);
            return;
         } catch (IOException var5) {
            ++var3;
            if (var3 == 10) {
               throw DataUtils.newMVStoreException(2, "Could not truncate file {0} to size {1}", this.fileName, var1, var5);
            }

            System.gc();
            Thread.yield();
         }
      }
   }

   public FileChannel getFile() {
      return this.file;
   }

   public FileChannel getEncryptedFile() {
      return this.encryptedFile;
   }

   public long getWriteCount() {
      return this.writeCount.get();
   }

   public long getWriteBytes() {
      return this.writeBytes.get();
   }

   public long getReadCount() {
      return this.readCount.get();
   }

   public long getReadBytes() {
      return this.readBytes.get();
   }

   public boolean isReadOnly() {
      return this.readOnly;
   }

   public int getDefaultRetentionTime() {
      return 45000;
   }

   public void markUsed(long var1, int var3) {
      this.freeSpace.markUsed(var1, var3);
   }

   long allocate(int var1, long var2, long var4) {
      return this.freeSpace.allocate(var1, var2, var4);
   }

   long predictAllocation(int var1, long var2, long var4) {
      return this.freeSpace.predictAllocation(var1, var2, var4);
   }

   boolean isFragmented() {
      return this.freeSpace.isFragmented();
   }

   public void free(long var1, int var3) {
      this.freeSpace.free(var1, var3);
   }

   public int getFillRate() {
      return this.freeSpace.getFillRate();
   }

   public int getProjectedFillRate(int var1) {
      return this.freeSpace.getProjectedFillRate(var1);
   }

   long getFirstFree() {
      return this.freeSpace.getFirstFree();
   }

   long getFileLengthInUse() {
      return this.freeSpace.getLastFree();
   }

   int getMovePriority(int var1) {
      return this.freeSpace.getMovePriority(var1);
   }

   long getAfterLastBlock() {
      return (long)this.freeSpace.getAfterLastBlock();
   }

   public void clear() {
      this.freeSpace.clear();
   }

   public String getFileName() {
      return this.fileName;
   }
}
