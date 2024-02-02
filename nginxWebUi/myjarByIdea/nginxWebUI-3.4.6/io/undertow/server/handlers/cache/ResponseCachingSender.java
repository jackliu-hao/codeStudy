package io.undertow.server.handlers.cache;

import io.undertow.io.IoCallback;
import io.undertow.io.Sender;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.xnio.Buffers;

public class ResponseCachingSender implements Sender {
   private final Sender delegate;
   private final DirectBufferCache.CacheEntry cacheEntry;
   private final long length;
   private long written;

   public ResponseCachingSender(Sender delegate, DirectBufferCache.CacheEntry cacheEntry, long length) {
      this.delegate = delegate;
      this.cacheEntry = cacheEntry;
      this.length = length;
   }

   public void send(ByteBuffer src, IoCallback callback) {
      ByteBuffer origSrc = src.duplicate();
      this.handleUpdate(origSrc);
      this.delegate.send(src, callback);
   }

   public void send(ByteBuffer[] srcs, IoCallback callback) {
      ByteBuffer[] origSrc = new ByteBuffer[srcs.length];
      long total = 0L;

      for(int i = 0; i < srcs.length; ++i) {
         origSrc[i] = srcs[i].duplicate();
         total += (long)origSrc[i].remaining();
      }

      this.handleUpdate(origSrc, total);
      this.delegate.send(srcs, callback);
   }

   public void send(ByteBuffer src) {
      ByteBuffer origSrc = src.duplicate();
      this.handleUpdate(origSrc);
      this.delegate.send(src);
   }

   public void send(ByteBuffer[] srcs) {
      ByteBuffer[] origSrc = new ByteBuffer[srcs.length];
      long total = 0L;

      for(int i = 0; i < srcs.length; ++i) {
         origSrc[i] = srcs[i].duplicate();
         total += (long)origSrc[i].remaining();
      }

      this.handleUpdate(origSrc, total);
      this.delegate.send(srcs);
   }

   public void send(String data, IoCallback callback) {
      this.handleUpdate(ByteBuffer.wrap(data.getBytes(StandardCharsets.UTF_8)));
      this.delegate.send(data, callback);
   }

   public void send(String data, Charset charset, IoCallback callback) {
      this.handleUpdate(ByteBuffer.wrap(data.getBytes(charset)));
      this.delegate.send(data, charset, callback);
   }

   public void send(String data) {
      this.handleUpdate(ByteBuffer.wrap(data.getBytes(StandardCharsets.UTF_8)));
      this.delegate.send(data);
   }

   public void send(String data, Charset charset) {
      this.handleUpdate(ByteBuffer.wrap(data.getBytes(charset)));
      this.delegate.send(data, charset);
   }

   public void transferFrom(FileChannel channel, IoCallback callback) {
      this.delegate.transferFrom(channel, callback);
   }

   public void close(IoCallback callback) {
      if (this.written != this.length) {
         this.cacheEntry.disable();
         this.cacheEntry.dereference();
      }

      this.delegate.close();
   }

   public void close() {
      if (this.written != this.length) {
         this.cacheEntry.disable();
         this.cacheEntry.dereference();
      }

      this.delegate.close();
   }

   private void handleUpdate(ByteBuffer origSrc) {
      LimitedBufferSlicePool.PooledByteBuffer[] pooled = this.cacheEntry.buffers();
      ByteBuffer[] buffers = new ByteBuffer[pooled.length];

      for(int i = 0; i < buffers.length; ++i) {
         buffers[i] = pooled[i].getBuffer();
      }

      this.written += (long)Buffers.copy(buffers, 0, buffers.length, origSrc);
      if (this.written == this.length) {
         ByteBuffer[] var8 = buffers;
         int var5 = buffers.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            ByteBuffer buffer = var8[var6];
            buffer.flip();
         }

         this.cacheEntry.enable();
      }

   }

   private void handleUpdate(ByteBuffer[] origSrc, long totalWritten) {
      LimitedBufferSlicePool.PooledByteBuffer[] pooled = this.cacheEntry.buffers();
      ByteBuffer[] buffers = new ByteBuffer[pooled.length];

      for(int i = 0; i < buffers.length; ++i) {
         buffers[i] = pooled[i].getBuffer();
      }

      long leftToCopy = totalWritten;

      for(int i = 0; i < origSrc.length; ++i) {
         ByteBuffer buf = origSrc[i];
         if ((long)buf.remaining() > leftToCopy) {
            buf.limit((int)((long)buf.position() + leftToCopy));
         }

         leftToCopy -= (long)buf.remaining();
         Buffers.copy(buffers, 0, buffers.length, buf);
         if (leftToCopy == 0L) {
            break;
         }
      }

      this.written += totalWritten;
      if (this.written == this.length) {
         ByteBuffer[] var13 = buffers;
         int var14 = buffers.length;

         for(int var10 = 0; var10 < var14; ++var10) {
            ByteBuffer buffer = var13[var10];
            buffer.flip();
         }

         this.cacheEntry.enable();
      }

   }
}
