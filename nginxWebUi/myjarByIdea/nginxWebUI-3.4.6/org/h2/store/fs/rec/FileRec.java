package org.h2.store.fs.rec;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Arrays;
import org.h2.store.fs.FileBase;

class FileRec extends FileBase {
   private final FilePathRec rec;
   private final FileChannel channel;
   private final String name;

   FileRec(FilePathRec var1, FileChannel var2, String var3) {
      this.rec = var1;
      this.channel = var2;
      this.name = var3;
   }

   public void implCloseChannel() throws IOException {
      this.channel.close();
   }

   public long position() throws IOException {
      return this.channel.position();
   }

   public long size() throws IOException {
      return this.channel.size();
   }

   public int read(ByteBuffer var1) throws IOException {
      return this.channel.read(var1);
   }

   public int read(ByteBuffer var1, long var2) throws IOException {
      return this.channel.read(var1, var2);
   }

   public FileChannel position(long var1) throws IOException {
      this.channel.position(var1);
      return this;
   }

   public FileChannel truncate(long var1) throws IOException {
      this.rec.log(7, this.name, (byte[])null, var1);
      this.channel.truncate(var1);
      return this;
   }

   public void force(boolean var1) throws IOException {
      this.channel.force(var1);
   }

   public int write(ByteBuffer var1) throws IOException {
      byte[] var2 = var1.array();
      int var3 = var1.remaining();
      int var4;
      if (var1.position() != 0 || var3 != var2.length) {
         var4 = var1.arrayOffset() + var1.position();
         var2 = Arrays.copyOfRange(var2, var4, var4 + var3);
      }

      var4 = this.channel.write(var1);
      this.rec.log(8, this.name, var2, this.channel.position());
      return var4;
   }

   public int write(ByteBuffer var1, long var2) throws IOException {
      byte[] var4 = var1.array();
      int var5 = var1.remaining();
      int var6;
      if (var1.position() != 0 || var5 != var4.length) {
         var6 = var1.arrayOffset() + var1.position();
         var4 = Arrays.copyOfRange(var4, var6, var6 + var5);
      }

      var6 = this.channel.write(var1, var2);
      this.rec.log(8, this.name, var4, var2);
      return var6;
   }

   public synchronized FileLock tryLock(long var1, long var3, boolean var5) throws IOException {
      return this.channel.tryLock(var1, var3, var5);
   }

   public String toString() {
      return this.name;
   }
}
