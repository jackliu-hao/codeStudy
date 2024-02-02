package org.h2.store.fs.niomapped;

import java.io.EOFException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.NonWritableChannelException;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.Paths;
import org.h2.engine.SysProperties;
import org.h2.store.fs.FileBaseDefault;
import org.h2.store.fs.FileUtils;
import org.h2.util.MemoryUnmapper;

class FileNioMapped extends FileBaseDefault {
   private static final int GC_TIMEOUT_MS = 10000;
   private final String name;
   private final FileChannel.MapMode mode;
   private FileChannel channel;
   private MappedByteBuffer mapped;
   private long fileLength;

   FileNioMapped(String var1, String var2) throws IOException {
      if ("r".equals(var2)) {
         this.mode = MapMode.READ_ONLY;
      } else {
         this.mode = MapMode.READ_WRITE;
      }

      this.name = var1;
      this.channel = FileChannel.open(Paths.get(var1), FileUtils.modeToOptions(var2), FileUtils.NO_ATTRIBUTES);
      this.reMap();
   }

   private void unMap() throws IOException {
      if (this.mapped != null) {
         this.mapped.force();
         if (SysProperties.NIO_CLEANER_HACK && MemoryUnmapper.unmap(this.mapped)) {
            this.mapped = null;
         } else {
            WeakReference var1 = new WeakReference(this.mapped);
            this.mapped = null;
            long var2 = System.nanoTime() + 10000000000L;

            while(var1.get() != null) {
               if (System.nanoTime() - var2 > 0L) {
                  throw new IOException("Timeout (10000 ms) reached while trying to GC mapped buffer");
               }

               System.gc();
               Thread.yield();
            }

         }
      }
   }

   private void reMap() throws IOException {
      if (this.mapped != null) {
         this.unMap();
      }

      this.fileLength = this.channel.size();
      checkFileSizeLimit(this.fileLength);
      this.mapped = this.channel.map(this.mode, 0L, this.fileLength);
      int var1 = this.mapped.limit();
      int var2 = this.mapped.capacity();
      if ((long)var1 >= this.fileLength && (long)var2 >= this.fileLength) {
         if (SysProperties.NIO_LOAD_MAPPED) {
            this.mapped.load();
         }

      } else {
         throw new IOException("Unable to map: length=" + var1 + " capacity=" + var2 + " length=" + this.fileLength);
      }
   }

   private static void checkFileSizeLimit(long var0) throws IOException {
      if (var0 > 2147483647L) {
         throw new IOException("File over 2GB is not supported yet when using this file system");
      }
   }

   public void implCloseChannel() throws IOException {
      if (this.channel != null) {
         this.unMap();
         this.channel.close();
         this.channel = null;
      }

   }

   public String toString() {
      return "nioMapped:" + this.name;
   }

   public synchronized long size() throws IOException {
      return this.fileLength;
   }

   public synchronized int read(ByteBuffer var1, long var2) throws IOException {
      checkFileSizeLimit(var2);

      try {
         int var4 = var1.remaining();
         if (var4 == 0) {
            return 0;
         } else {
            var4 = (int)Math.min((long)var4, this.fileLength - var2);
            if (var4 <= 0) {
               return -1;
            } else {
               this.mapped.position((int)var2);
               this.mapped.get(var1.array(), var1.arrayOffset() + var1.position(), var4);
               var1.position(var1.position() + var4);
               long var10000 = var2 + (long)var4;
               return var4;
            }
         }
      } catch (BufferUnderflowException | IllegalArgumentException var6) {
         EOFException var5 = new EOFException("EOF");
         var5.initCause(var6);
         throw var5;
      }
   }

   protected void implTruncate(long var1) throws IOException {
      if (this.mode == MapMode.READ_ONLY) {
         throw new NonWritableChannelException();
      } else {
         if (var1 < this.size()) {
            this.setFileLength(var1);
         }

      }
   }

   public synchronized void setFileLength(long var1) throws IOException {
      if (this.mode == MapMode.READ_ONLY) {
         throw new NonWritableChannelException();
      } else {
         checkFileSizeLimit(var1);
         this.unMap();
         int var3 = 0;

         while(true) {
            try {
               long var4 = this.channel.size();
               if (var4 >= var1) {
                  this.channel.truncate(var1);
               } else {
                  this.channel.write(ByteBuffer.wrap(new byte[1]), var1 - 1L);
               }
               break;
            } catch (IOException var6) {
               if (var3 > 16 || !var6.toString().contains("user-mapped section open")) {
                  throw var6;
               }

               System.gc();
               ++var3;
            }
         }

         this.reMap();
      }
   }

   public void force(boolean var1) throws IOException {
      this.mapped.force();
      this.channel.force(var1);
   }

   public synchronized int write(ByteBuffer var1, long var2) throws IOException {
      checkFileSizeLimit(var2);
      int var4 = var1.remaining();
      if ((long)this.mapped.capacity() < var2 + (long)var4) {
         this.setFileLength(var2 + (long)var4);
      }

      this.mapped.position((int)var2);
      this.mapped.put(var1);
      return var4;
   }

   public synchronized FileLock tryLock(long var1, long var3, boolean var5) throws IOException {
      return this.channel.tryLock(var1, var3, var5);
   }
}
