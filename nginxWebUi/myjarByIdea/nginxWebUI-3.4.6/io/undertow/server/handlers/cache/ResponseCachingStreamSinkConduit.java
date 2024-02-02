package io.undertow.server.handlers.cache;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import org.xnio.Buffers;
import org.xnio.IoUtils;
import org.xnio.channels.StreamSourceChannel;
import org.xnio.conduits.AbstractStreamSinkConduit;
import org.xnio.conduits.ConduitWritableByteChannel;
import org.xnio.conduits.Conduits;
import org.xnio.conduits.StreamSinkConduit;

public class ResponseCachingStreamSinkConduit extends AbstractStreamSinkConduit<StreamSinkConduit> {
   private final DirectBufferCache.CacheEntry cacheEntry;
   private final long length;
   private long written;

   public ResponseCachingStreamSinkConduit(StreamSinkConduit next, DirectBufferCache.CacheEntry cacheEntry, long length) {
      super(next);
      this.cacheEntry = cacheEntry;
      this.length = length;
      LimitedBufferSlicePool.PooledByteBuffer[] var5 = cacheEntry.buffers();
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         LimitedBufferSlicePool.PooledByteBuffer buffer = var5[var7];
         buffer.getBuffer().clear();
      }

   }

   public long transferFrom(FileChannel src, long position, long count) throws IOException {
      return src.transferTo(position, count, new ConduitWritableByteChannel(this));
   }

   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
      return IoUtils.transfer(source, count, throughBuffer, new ConduitWritableByteChannel(this));
   }

   public int write(ByteBuffer src) throws IOException {
      ByteBuffer origSrc = src.duplicate();
      int totalWritten = super.write(src);
      if (totalWritten > 0) {
         LimitedBufferSlicePool.PooledByteBuffer[] pooled = this.cacheEntry.buffers();
         ByteBuffer[] buffers = new ByteBuffer[pooled.length];

         for(int i = 0; i < buffers.length; ++i) {
            buffers[i] = pooled[i].getBuffer();
         }

         origSrc.limit(origSrc.position() + totalWritten);
         this.written += (long)Buffers.copy(buffers, 0, buffers.length, origSrc);
         if (this.written == this.length) {
            ByteBuffer[] var10 = buffers;
            int var7 = buffers.length;

            for(int var8 = 0; var8 < var7; ++var8) {
               ByteBuffer buffer = var10[var8];
               buffer.flip();
            }

            this.cacheEntry.enable();
         }
      }

      return totalWritten;
   }

   public long write(ByteBuffer[] srcs, int offs, int len) throws IOException {
      ByteBuffer[] origSrc = new ByteBuffer[srcs.length];

      for(int i = 0; i < srcs.length; ++i) {
         origSrc[i] = srcs[i].duplicate();
      }

      long totalWritten = super.write(srcs, offs, len);
      if (totalWritten > 0L) {
         LimitedBufferSlicePool.PooledByteBuffer[] pooled = this.cacheEntry.buffers();
         ByteBuffer[] buffers = new ByteBuffer[pooled.length];

         for(int i = 0; i < buffers.length; ++i) {
            buffers[i] = pooled[i].getBuffer();
         }

         long leftToCopy = totalWritten;

         for(int i = 0; i < len; ++i) {
            ByteBuffer buf = origSrc[offs + i];
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
            ByteBuffer[] var17 = buffers;
            int var18 = buffers.length;

            for(int var13 = 0; var13 < var18; ++var13) {
               ByteBuffer buffer = var17[var13];
               buffer.flip();
            }

            this.cacheEntry.enable();
         }
      }

      return totalWritten;
   }

   public void terminateWrites() throws IOException {
      if (this.written != this.length) {
         this.cacheEntry.disable();
         this.cacheEntry.dereference();
      }

      super.terminateWrites();
   }

   public void truncateWrites() throws IOException {
      if (this.written != this.length) {
         this.cacheEntry.disable();
         this.cacheEntry.dereference();
      }

      super.truncateWrites();
   }

   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
      return Conduits.writeFinalBasic(this, srcs, offset, length);
   }

   public int writeFinal(ByteBuffer src) throws IOException {
      return Conduits.writeFinalBasic(this, src);
   }
}
