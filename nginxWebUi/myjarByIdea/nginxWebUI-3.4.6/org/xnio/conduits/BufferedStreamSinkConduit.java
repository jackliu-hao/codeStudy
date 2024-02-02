package org.xnio.conduits;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import org.xnio.Buffers;
import org.xnio.Pooled;
import org.xnio.channels.StreamSourceChannel;

public final class BufferedStreamSinkConduit extends AbstractStreamSinkConduit<StreamSinkConduit> {
   private final Pooled<ByteBuffer> pooledBuffer;
   private boolean terminate;

   public BufferedStreamSinkConduit(StreamSinkConduit next, Pooled<ByteBuffer> pooledBuffer) {
      super(next);
      this.pooledBuffer = pooledBuffer;
   }

   public long transferFrom(FileChannel src, long position, long count) throws IOException {
      return this.flushLocal() ? super.transferFrom(src, position, count) : 0L;
   }

   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
      if (this.flushLocal()) {
         return super.transferFrom(source, count, throughBuffer);
      } else {
         throughBuffer.limit(0);
         return 0L;
      }
   }

   public int write(ByteBuffer src) throws IOException {
      try {
         ByteBuffer buffer = (ByteBuffer)this.pooledBuffer.getResource();
         int pos = buffer.position();
         int lim = buffer.limit();
         int srcRem = src.remaining();
         int ourRem = lim - pos;
         if (srcRem < ourRem) {
            buffer.put(src);
            return srcRem;
         } else if (buffer.position() == 0) {
            int res = super.write(src);
            if (srcRem > res) {
               int cnt = Buffers.copy(buffer, src);
               return res + cnt;
            } else {
               return res;
            }
         } else {
            buffer.flip();

            try {
               super.write(new ByteBuffer[]{buffer, src}, 0, 2);
            } finally {
               buffer.compact();
            }

            if (src.hasRemaining()) {
               Buffers.copy(buffer, src);
            }

            return srcRem - src.remaining();
         }
      } catch (IllegalStateException var13) {
         throw new ClosedChannelException();
      }
   }

   public long write(ByteBuffer[] srcs, int offs, int len) throws IOException {
      if (len == 0) {
         return 0L;
      } else if (len == 1) {
         return (long)this.write(srcs[offs]);
      } else {
         try {
            ByteBuffer buffer = (ByteBuffer)this.pooledBuffer.getResource();
            int pos = buffer.position();
            int lim = buffer.limit();
            long srcRem = Buffers.remaining(srcs, offs, len);
            int ourRem = lim - pos;
            if (srcRem >= (long)ourRem) {
               if (buffer.position() == 0) {
                  long res = super.write(srcs, offs, len);
                  if (srcRem > res) {
                     int cnt = Buffers.copy(buffer, srcs, offs, len);
                     return res + (long)cnt;
                  } else {
                     return res;
                  }
               } else {
                  buffer.flip();

                  try {
                     ByteBuffer[] buffers;
                     if (offs > 0) {
                        buffers = (ByteBuffer[])Arrays.copyOfRange(srcs, offs - 1, offs + len);
                     } else {
                        buffers = new ByteBuffer[len + 1];
                        System.arraycopy(srcs, offs, buffers, 1, len);
                     }

                     buffers[0] = buffer;
                     super.write(buffers, 0, buffers.length);
                  } finally {
                     buffer.compact();
                  }

                  Buffers.copy(buffer, srcs, offs, len);
                  return srcRem - Buffers.remaining(srcs, offs, len);
               }
            } else {
               for(int i = 0; i < len; ++i) {
                  buffer.put(srcs[i]);
               }

               return srcRem;
            }
         } catch (IllegalStateException var17) {
            throw new ClosedChannelException();
         }
      }
   }

   private boolean flushLocal() throws IOException {
      try {
         ByteBuffer buffer = (ByteBuffer)this.pooledBuffer.getResource();
         if (buffer.position() <= 0) {
            return true;
         } else {
            buffer.flip();

            boolean var2;
            try {
               do {
                  super.write(buffer);
               } while(buffer.hasRemaining());

               if (this.terminate) {
                  this.pooledBuffer.free();
               }

               var2 = true;
            } finally {
               buffer.compact();
            }

            return var2;
         }
      } catch (IllegalStateException var7) {
         return true;
      }
   }

   public int writeFinal(ByteBuffer src) throws IOException {
      return Conduits.writeFinalBasic(this, src);
   }

   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
      return Conduits.writeFinalBasic(this, srcs, offset, length);
   }

   public boolean flush() throws IOException {
      return this.flushLocal() && super.flush();
   }

   public void truncateWrites() throws IOException {
      this.pooledBuffer.free();
      super.truncateWrites();
   }

   public void terminateWrites() throws IOException {
      this.terminate = true;
      super.terminateWrites();
   }
}
