package org.h2.store.fs.async;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import org.h2.store.fs.FileBaseDefault;
import org.h2.store.fs.FileUtils;

class FileAsync extends FileBaseDefault {
   private final String name;
   private final AsynchronousFileChannel channel;

   private static <T> T complete(Future<T> var0) throws IOException {
      boolean var1 = false;

      while(true) {
         try {
            Object var2 = var0.get();
            if (var1) {
               Thread.currentThread().interrupt();
            }

            return var2;
         } catch (InterruptedException var3) {
            var1 = true;
         } catch (ExecutionException var4) {
            throw new IOException(var4.getCause());
         }
      }
   }

   FileAsync(String var1, String var2) throws IOException {
      this.name = var1;
      this.channel = AsynchronousFileChannel.open(Paths.get(var1), FileUtils.modeToOptions(var2), (ExecutorService)null, FileUtils.NO_ATTRIBUTES);
   }

   public void implCloseChannel() throws IOException {
      this.channel.close();
   }

   public long size() throws IOException {
      return this.channel.size();
   }

   public int read(ByteBuffer var1, long var2) throws IOException {
      return (Integer)complete(this.channel.read(var1, var2));
   }

   public int write(ByteBuffer var1, long var2) throws IOException {
      return (Integer)complete(this.channel.write(var1, var2));
   }

   protected void implTruncate(long var1) throws IOException {
      this.channel.truncate(var1);
   }

   public void force(boolean var1) throws IOException {
      this.channel.force(var1);
   }

   public FileLock tryLock(long var1, long var3, boolean var5) throws IOException {
      return this.channel.tryLock(var1, var3, var5);
   }

   public String toString() {
      return "async:" + this.name;
   }
}
