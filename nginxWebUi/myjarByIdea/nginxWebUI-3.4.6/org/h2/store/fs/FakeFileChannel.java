package org.h2.store.fs;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public class FakeFileChannel extends FileChannel {
   public static final FakeFileChannel INSTANCE = new FakeFileChannel();

   private FakeFileChannel() {
   }

   protected void implCloseChannel() throws IOException {
      throw new IOException();
   }

   public FileLock lock(long var1, long var3, boolean var5) throws IOException {
      throw new IOException();
   }

   public MappedByteBuffer map(FileChannel.MapMode var1, long var2, long var4) throws IOException {
      throw new IOException();
   }

   public long position() throws IOException {
      throw new IOException();
   }

   public FileChannel position(long var1) throws IOException {
      throw new IOException();
   }

   public int read(ByteBuffer var1) throws IOException {
      throw new IOException();
   }

   public int read(ByteBuffer var1, long var2) throws IOException {
      throw new IOException();
   }

   public long read(ByteBuffer[] var1, int var2, int var3) throws IOException {
      throw new IOException();
   }

   public long size() throws IOException {
      throw new IOException();
   }

   public long transferFrom(ReadableByteChannel var1, long var2, long var4) throws IOException {
      throw new IOException();
   }

   public long transferTo(long var1, long var3, WritableByteChannel var5) throws IOException {
      throw new IOException();
   }

   public FileChannel truncate(long var1) throws IOException {
      throw new IOException();
   }

   public FileLock tryLock(long var1, long var3, boolean var5) throws IOException {
      throw new IOException();
   }

   public int write(ByteBuffer var1) throws IOException {
      throw new IOException();
   }

   public int write(ByteBuffer var1, long var2) throws IOException {
      throw new IOException();
   }

   public long write(ByteBuffer[] var1, int var2, int var3) throws IOException {
      throw new IOException();
   }

   public void force(boolean var1) throws IOException {
      throw new IOException();
   }
}
