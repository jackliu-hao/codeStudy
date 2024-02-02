package org.xnio.conduits;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import org.xnio.Buffers;
import org.xnio.Pooled;
import org.xnio._private.Messages;

public final class FramingMessageSourceConduit extends AbstractSourceConduit<StreamSourceConduit> implements MessageSourceConduit {
   private final Pooled<ByteBuffer> receiveBuffer;
   private boolean ready;

   public FramingMessageSourceConduit(StreamSourceConduit next, Pooled<ByteBuffer> receiveBuffer) {
      super(next);
      this.receiveBuffer = receiveBuffer;
   }

   public void resumeReads() {
      if (this.ready) {
         ((StreamSourceConduit)this.next).wakeupReads();
      } else {
         ((StreamSourceConduit)this.next).resumeReads();
      }

   }

   public void awaitReadable(long time, TimeUnit timeUnit) throws IOException {
      if (!this.ready) {
         ((StreamSourceConduit)this.next).awaitReadable(time, timeUnit);
      }

   }

   public void awaitReadable() throws IOException {
      if (!this.ready) {
         ((StreamSourceConduit)this.next).awaitReadable();
      }

   }

   public void terminateReads() throws IOException {
      this.receiveBuffer.free();
      ((StreamSourceConduit)this.next).terminateReads();
   }

   public int receive(ByteBuffer dst) throws IOException {
      ByteBuffer receiveBuffer = (ByteBuffer)this.receiveBuffer.getResource();

      int res;
      do {
         res = ((StreamSourceConduit)this.next).read(receiveBuffer);
      } while(res > 0);

      if (receiveBuffer.position() < 4) {
         if (res == -1) {
            receiveBuffer.clear();
         }

         this.ready = false;
         return res;
      } else {
         receiveBuffer.flip();

         int var5;
         try {
            int length = receiveBuffer.getInt();
            if (length < 0 || length > receiveBuffer.capacity() - 4) {
               Buffers.unget(receiveBuffer, 4);
               throw Messages.msg.recvInvalidMsgLength(length);
            }

            if (receiveBuffer.remaining() >= length) {
               if (dst.hasRemaining()) {
                  var5 = Buffers.copy(length, dst, receiveBuffer);
                  return var5;
               }

               Buffers.skip(receiveBuffer, length);
               byte var9 = 0;
               return var9;
            }

            if (res == -1) {
               receiveBuffer.clear();
            } else {
               Buffers.unget(receiveBuffer, 4);
            }

            this.ready = false;
            var5 = res;
         } finally {
            if (res != -1) {
               receiveBuffer.compact();
               if (receiveBuffer.position() >= 4 && receiveBuffer.position() >= 4 + receiveBuffer.getInt(0)) {
                  this.ready = true;
               }
            }

         }

         return var5;
      }
   }

   public long receive(ByteBuffer[] dsts, int offs, int len) throws IOException {
      ByteBuffer receiveBuffer = (ByteBuffer)this.receiveBuffer.getResource();

      int res;
      do {
         res = ((StreamSourceConduit)this.next).read(receiveBuffer);
      } while(res > 0);

      if (receiveBuffer.position() < 4) {
         if (res == -1) {
            receiveBuffer.clear();
         }

         this.ready = false;
         return (long)res;
      } else {
         receiveBuffer.flip();

         long var7;
         try {
            int length = receiveBuffer.getInt();
            if (length < 0 || length > receiveBuffer.capacity() - 4) {
               Buffers.unget(receiveBuffer, 4);
               throw Messages.msg.recvInvalidMsgLength(length);
            }

            if (receiveBuffer.remaining() >= length) {
               if (Buffers.hasRemaining(dsts, offs, len)) {
                  var7 = (long)Buffers.copy(length, dsts, offs, len, receiveBuffer);
                  return var7;
               }

               Buffers.skip(receiveBuffer, length);
               var7 = 0L;
               return var7;
            }

            if (res == -1) {
               receiveBuffer.clear();
            } else {
               Buffers.unget(receiveBuffer, 4);
            }

            this.ready = false;
            var7 = (long)res;
         } finally {
            if (res != -1) {
               receiveBuffer.compact();
               if (receiveBuffer.position() >= 4 && receiveBuffer.position() >= 4 + receiveBuffer.getInt(0)) {
                  this.ready = true;
               }
            }

         }

         return var7;
      }
   }
}
