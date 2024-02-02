package org.h2.store.fs;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public abstract class FileBase extends FileChannel {
   public synchronized int read(ByteBuffer var1, long var2) throws IOException {
      long var4 = this.position();
      this.position(var2);
      int var6 = this.read(var1);
      this.position(var4);
      return var6;
   }

   public synchronized int write(ByteBuffer var1, long var2) throws IOException {
      long var4 = this.position();
      this.position(var2);
      int var6 = this.write(var1);
      this.position(var4);
      return var6;
   }

   public void force(boolean var1) throws IOException {
   }

   protected void implCloseChannel() throws IOException {
   }

   public FileLock lock(long var1, long var3, boolean var5) throws IOException {
      throw new UnsupportedOperationException();
   }

   public MappedByteBuffer map(FileChannel.MapMode var1, long var2, long var4) throws IOException {
      throw new UnsupportedOperationException();
   }

   public long read(ByteBuffer[] var1, int var2, int var3) throws IOException {
      throw new UnsupportedOperationException();
   }

   public long transferFrom(ReadableByteChannel var1, long var2, long var4) throws IOException {
      throw new UnsupportedOperationException();
   }

   public long transferTo(long var1, long var3, WritableByteChannel var5) throws IOException {
      throw new UnsupportedOperationException();
   }

   public FileLock tryLock(long var1, long var3, boolean var5) throws IOException {
      throw new UnsupportedOperationException();
   }

   public long write(ByteBuffer[] var1, int var2, int var3) throws IOException {
      throw new UnsupportedOperationException();
   }
}
