package io.undertow.websockets.core.function;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public class ChannelFunctionFileChannel extends FileChannel {
   private final ChannelFunction[] functions;
   private final FileChannel channel;

   public ChannelFunctionFileChannel(FileChannel channel, ChannelFunction... functions) {
      this.channel = channel;
      this.functions = functions;
   }

   public long position() throws IOException {
      return this.channel.position();
   }

   public FileChannel position(long newPosition) throws IOException {
      this.channel.position(newPosition);
      return this;
   }

   public long size() throws IOException {
      return this.channel.size();
   }

   public FileChannel truncate(long size) throws IOException {
      this.channel.truncate(size);
      return this;
   }

   public void force(boolean metaData) throws IOException {
      this.channel.force(metaData);
   }

   public MappedByteBuffer map(FileChannel.MapMode mode, long position, long size) throws IOException {
      return this.channel.map(mode, position, size);
   }

   public FileLock lock(long position, long size, boolean shared) throws IOException {
      return this.channel.lock(position, size, shared);
   }

   public FileLock tryLock(long position, long size, boolean shared) throws IOException {
      return this.channel.tryLock(position, size, shared);
   }

   protected void implCloseChannel() throws IOException {
      this.channel.close();
   }

   public int write(ByteBuffer src, long position) throws IOException {
      this.beforeWriting(src);
      return this.channel.write(src, position);
   }

   public int read(ByteBuffer dst) throws IOException {
      int pos = dst.position();
      int r = this.channel.read(dst);
      if (r > 0) {
         this.afterReading(dst, pos, r);
      }

      return r;
   }

   public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
      int[] positions = new int[length];

      for(int i = 0; i < positions.length; ++i) {
         positions[i] = dsts[i].position();
      }

      long r = this.channel.read(dsts, offset, length);
      if (r > 0L) {
         for(int i = offset; i < length; ++i) {
            ByteBuffer dst = dsts[i];
            this.afterReading(dst, positions[i], dst.position());
         }
      }

      return r;
   }

   public int write(ByteBuffer src) throws IOException {
      this.beforeWriting(src);
      return this.channel.write(src);
   }

   public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
      for(int i = offset; i < length; ++i) {
         this.beforeWriting(srcs[i]);
      }

      return this.channel.write(srcs, offset, length);
   }

   public int read(ByteBuffer dst, long position) throws IOException {
      int pos = dst.position();
      int r = this.channel.read(dst, position);
      if (r > 0) {
         this.afterReading(dst, pos, r);
      }

      return r;
   }

   public long transferTo(long position, long count, WritableByteChannel target) throws IOException {
      return this.channel.transferTo(position, count, new ChannelFunctionWritableByteChannel(target, this.functions));
   }

   public long transferFrom(ReadableByteChannel src, long position, long count) throws IOException {
      return this.channel.transferFrom(new ChannelFunctionReadableByteChannel(this.channel, this.functions), position, count);
   }

   private void beforeWriting(ByteBuffer buffer) throws IOException {
      ChannelFunction[] var2 = this.functions;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         ChannelFunction func = var2[var4];
         int pos = buffer.position();
         func.beforeWrite(buffer, pos, buffer.limit() - pos);
      }

   }

   private void afterReading(ByteBuffer buffer, int position, int length) throws IOException {
      ChannelFunction[] var4 = this.functions;
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         ChannelFunction func = var4[var6];
         func.afterRead(buffer, position, length);
      }

   }
}
