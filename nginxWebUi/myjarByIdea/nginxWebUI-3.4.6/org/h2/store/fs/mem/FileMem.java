package org.h2.store.fs.mem;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileLock;
import java.nio.channels.NonWritableChannelException;
import org.h2.store.fs.FakeFileChannel;
import org.h2.store.fs.FileBaseDefault;

class FileMem extends FileBaseDefault {
   final FileMemData data;
   private final boolean readOnly;
   private volatile boolean closed;

   FileMem(FileMemData var1, boolean var2) {
      this.data = var1;
      this.readOnly = var2;
   }

   public long size() {
      return this.data.length();
   }

   protected void implTruncate(long var1) throws IOException {
      if (this.readOnly) {
         throw new NonWritableChannelException();
      } else if (this.closed) {
         throw new ClosedChannelException();
      } else {
         if (var1 < this.size()) {
            this.data.touch(this.readOnly);
            this.data.truncate(var1);
         }

      }
   }

   public int write(ByteBuffer var1, long var2) throws IOException {
      if (this.closed) {
         throw new ClosedChannelException();
      } else if (this.readOnly) {
         throw new NonWritableChannelException();
      } else {
         int var4 = var1.remaining();
         if (var4 == 0) {
            return 0;
         } else {
            this.data.touch(this.readOnly);
            this.data.readWrite(var2, var1.array(), var1.arrayOffset() + var1.position(), var4, true);
            var1.position(var1.position() + var4);
            return var4;
         }
      }
   }

   public int read(ByteBuffer var1, long var2) throws IOException {
      if (this.closed) {
         throw new ClosedChannelException();
      } else {
         int var4 = var1.remaining();
         if (var4 == 0) {
            return 0;
         } else {
            long var5 = this.data.readWrite(var2, var1.array(), var1.arrayOffset() + var1.position(), var4, false);
            var4 = (int)(var5 - var2);
            if (var4 <= 0) {
               return -1;
            } else {
               var1.position(var1.position() + var4);
               return var4;
            }
         }
      }
   }

   public void implCloseChannel() throws IOException {
      this.closed = true;
   }

   public void force(boolean var1) throws IOException {
   }

   public FileLock tryLock(long var1, long var3, boolean var5) throws IOException {
      if (this.closed) {
         throw new ClosedChannelException();
      } else {
         if (var5) {
            if (!this.data.lockShared()) {
               return null;
            }
         } else if (!this.data.lockExclusive()) {
            return null;
         }

         return new FileLock(FakeFileChannel.INSTANCE, var1, var3, var5) {
            public boolean isValid() {
               return true;
            }

            public void release() throws IOException {
               FileMem.this.data.unlock();
            }
         };
      }
   }

   public String toString() {
      return this.closed ? "<closed>" : this.data.getName();
   }
}
