package org.xnio.conduits;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Arrays;
import org.xnio.Buffers;
import org.xnio.Pooled;
import org.xnio.channels.StreamSinkChannel;

public final class BufferedStreamSourceConduit extends AbstractStreamSourceConduit<StreamSourceConduit> {
   private final Pooled<ByteBuffer> pooledBuffer;

   public BufferedStreamSourceConduit(StreamSourceConduit next, Pooled<ByteBuffer> pooledBuffer) {
      super(next);
      this.pooledBuffer = pooledBuffer;
   }

   public long transferTo(long position, long count, FileChannel target) throws IOException {
      try {
         ByteBuffer buffer = (ByteBuffer)this.pooledBuffer.getResource();
         int lim = buffer.limit();
         int pos = buffer.position();
         int rem = lim - pos;
         if (rem > 0) {
            if ((long)rem > count) {
               buffer.limit(pos + (int)count);

               long var10;
               try {
                  var10 = (long)target.write(buffer, position);
               } finally {
                  buffer.limit(lim);
               }

               return var10;
            } else {
               return (long)target.write(buffer, position);
            }
         } else {
            return super.transferTo(position, count, target);
         }
      } catch (IllegalStateException var16) {
         return 0L;
      }
   }

   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
      try {
         ByteBuffer buffer = (ByteBuffer)this.pooledBuffer.getResource();
         int lim = buffer.limit();
         int pos = buffer.position();
         int rem = lim - pos;
         throughBuffer.clear();

         long t;
         for(t = 0L; rem > 0; rem = lim - pos) {
            int res;
            if ((long)rem > count) {
               buffer.limit(pos + (int)count);

               try {
                  t += (long)(res = target.write(buffer));
               } finally {
                  buffer.limit(lim);
               }
            } else {
               t += (long)(res = target.write(buffer));
            }

            if (res == 0) {
               return t;
            }

            pos = buffer.position();
         }

         long lres = Conduits.transfer((StreamSourceConduit)((StreamSourceConduit)this.next), count, throughBuffer, (WritableByteChannel)target);
         if (lres > 0L) {
            t += lres;
         }

         return t == 0L && lres == -1L ? -1L : t;
      } catch (IllegalStateException var17) {
         return -1L;
      }
   }

   public int read(ByteBuffer dst) throws IOException {
      try {
         ByteBuffer buffer = (ByteBuffer)this.pooledBuffer.getResource();
         int lim = buffer.limit();
         int pos = buffer.position();
         int rem = lim - pos;
         if (rem > 0) {
            return Buffers.copy(dst, buffer);
         } else {
            int dstRem = dst.remaining();
            buffer.clear();

            try {
               long rres = ((StreamSourceConduit)this.next).read(new ByteBuffer[]{dst, buffer}, 0, 2);
               if (rres == -1L) {
                  byte var9 = -1;
                  return var9;
               }
            } finally {
               buffer.flip();
            }

            return dst.remaining() - dstRem;
         }
      } catch (IllegalStateException var14) {
         return -1;
      }
   }

   public long read(ByteBuffer[] dsts, int offs, int len) throws IOException {
      if (len == 0) {
         return 0L;
      } else if (len == 1) {
         return (long)this.read(dsts[offs]);
      } else {
         try {
            ByteBuffer buffer = (ByteBuffer)this.pooledBuffer.getResource();
            int lim = buffer.limit();
            int pos = buffer.position();
            int rem = lim - pos;
            if (rem > 0) {
               return (long)Buffers.copy(dsts, offs, len, buffer);
            } else {
               long dstRem = Buffers.remaining(dsts, offs, len);
               buffer.clear();

               long var13;
               try {
                  ByteBuffer[] buffers = (ByteBuffer[])Arrays.copyOfRange(dsts, offs, offs + len + 1);
                  buffers[buffers.length - 1] = buffer;
                  long rres = ((StreamSourceConduit)this.next).read(buffers, 0, buffers.length);
                  if (rres != -1L) {
                     return Buffers.remaining(dsts, offs, len) - dstRem;
                  }

                  var13 = -1L;
               } finally {
                  buffer.flip();
               }

               return var13;
            }
         } catch (IllegalStateException var19) {
            return -1L;
         }
      }
   }

   public void terminateReads() throws IOException {
      this.pooledBuffer.free();
      super.terminateReads();
   }
}
