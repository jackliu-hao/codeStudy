package org.h2.store.fs.retry;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import org.h2.store.fs.FileBase;
import org.h2.store.fs.FileUtils;

class FileRetryOnInterrupt extends FileBase {
   private final String fileName;
   private final String mode;
   private FileChannel channel;
   private FileLockRetry lock;

   FileRetryOnInterrupt(String var1, String var2) throws IOException {
      this.fileName = var1;
      this.mode = var2;
      this.open();
   }

   private void open() throws IOException {
      this.channel = FileUtils.open(this.fileName, this.mode);
   }

   private void reopen(int var1, IOException var2) throws IOException {
      if (var1 > 20) {
         throw var2;
      } else if (!(var2 instanceof ClosedByInterruptException) && !(var2 instanceof ClosedChannelException)) {
         throw var2;
      } else {
         Thread.interrupted();
         FileChannel var3 = this.channel;
         synchronized(this) {
            if (var3 == this.channel) {
               this.open();
               this.reLock();
            }

         }
      }
   }

   private void reLock() throws IOException {
      if (this.lock != null) {
         try {
            this.lock.base.release();
         } catch (IOException var2) {
         }

         FileLock var1 = this.channel.tryLock(this.lock.position(), this.lock.size(), this.lock.isShared());
         if (var1 == null) {
            throw new IOException("Re-locking failed");
         } else {
            this.lock.base = var1;
         }
      }
   }

   public void implCloseChannel() throws IOException {
      try {
         this.channel.close();
      } catch (IOException var2) {
      }

   }

   public long position() throws IOException {
      int var1 = 0;

      while(true) {
         try {
            return this.channel.position();
         } catch (IOException var3) {
            this.reopen(var1, var3);
            ++var1;
         }
      }
   }

   public long size() throws IOException {
      int var1 = 0;

      while(true) {
         try {
            return this.channel.size();
         } catch (IOException var3) {
            this.reopen(var1, var3);
            ++var1;
         }
      }
   }

   public int read(ByteBuffer var1) throws IOException {
      long var2 = this.position();
      int var4 = 0;

      while(true) {
         try {
            return this.channel.read(var1);
         } catch (IOException var6) {
            this.reopen(var4, var6);
            this.position(var2);
            ++var4;
         }
      }
   }

   public int read(ByteBuffer var1, long var2) throws IOException {
      int var4 = 0;

      while(true) {
         try {
            return this.channel.read(var1, var2);
         } catch (IOException var6) {
            this.reopen(var4, var6);
            ++var4;
         }
      }
   }

   public FileChannel position(long var1) throws IOException {
      int var3 = 0;

      while(true) {
         try {
            this.channel.position(var1);
            return this;
         } catch (IOException var5) {
            this.reopen(var3, var5);
            ++var3;
         }
      }
   }

   public FileChannel truncate(long var1) throws IOException {
      int var3 = 0;

      while(true) {
         try {
            this.channel.truncate(var1);
            return this;
         } catch (IOException var5) {
            this.reopen(var3, var5);
            ++var3;
         }
      }
   }

   public void force(boolean var1) throws IOException {
      int var2 = 0;

      while(true) {
         try {
            this.channel.force(var1);
            return;
         } catch (IOException var4) {
            this.reopen(var2, var4);
            ++var2;
         }
      }
   }

   public int write(ByteBuffer var1) throws IOException {
      long var2 = this.position();
      int var4 = 0;

      while(true) {
         try {
            return this.channel.write(var1);
         } catch (IOException var6) {
            this.reopen(var4, var6);
            this.position(var2);
            ++var4;
         }
      }
   }

   public int write(ByteBuffer var1, long var2) throws IOException {
      int var4 = 0;

      while(true) {
         try {
            return this.channel.write(var1, var2);
         } catch (IOException var6) {
            this.reopen(var4, var6);
            ++var4;
         }
      }
   }

   public synchronized FileLock tryLock(long var1, long var3, boolean var5) throws IOException {
      FileLock var6 = this.channel.tryLock(var1, var3, var5);
      if (var6 == null) {
         return null;
      } else {
         this.lock = new FileLockRetry(var6, this);
         return this.lock;
      }
   }

   public String toString() {
      return "retry:" + this.fileName;
   }

   static class FileLockRetry extends FileLock {
      FileLock base;

      protected FileLockRetry(FileLock var1, FileChannel var2) {
         super(var2, var1.position(), var1.size(), var1.isShared());
         this.base = var1;
      }

      public boolean isValid() {
         return this.base.isValid();
      }

      public void release() throws IOException {
         this.base.release();
      }
   }
}
