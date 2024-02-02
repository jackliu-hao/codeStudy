package org.xnio.conduits;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.xnio.Buffers;
import org.xnio.Pooled;
import org.xnio._private.Messages;

public final class FramingMessageSinkConduit extends AbstractSinkConduit<StreamSinkConduit> implements MessageSinkConduit {
   private final boolean longLengths;
   private final Pooled<ByteBuffer> transmitBuffer;

   public FramingMessageSinkConduit(StreamSinkConduit next, boolean longLengths, Pooled<ByteBuffer> transmitBuffer) {
      super(next);
      this.longLengths = longLengths;
      this.transmitBuffer = transmitBuffer;
   }

   public boolean send(ByteBuffer src) throws IOException {
      if (!src.hasRemaining()) {
         return false;
      } else {
         ByteBuffer transmitBuffer = (ByteBuffer)this.transmitBuffer.getResource();
         int remaining = src.remaining();
         boolean longLengths = this.longLengths;
         int lengthFieldSize = longLengths ? 4 : 2;
         if (remaining > transmitBuffer.capacity() - lengthFieldSize || !longLengths && remaining > 65535) {
            throw Messages.msg.txMsgTooLarge();
         } else if (transmitBuffer.remaining() < lengthFieldSize + remaining && !this.writeBuffer()) {
            return false;
         } else {
            if (longLengths) {
               transmitBuffer.putInt(remaining);
            } else {
               transmitBuffer.putShort((short)remaining);
            }

            transmitBuffer.put(src);
            this.writeBuffer();
            return true;
         }
      }
   }

   public boolean send(ByteBuffer[] srcs, int offs, int len) throws IOException {
      if (len == 1) {
         return this.send(srcs[offs]);
      } else if (!Buffers.hasRemaining(srcs, offs, len)) {
         return false;
      } else {
         ByteBuffer transmitBuffer = (ByteBuffer)this.transmitBuffer.getResource();
         long remaining = Buffers.remaining(srcs, offs, len);
         boolean longLengths = this.longLengths;
         int lengthFieldSize = longLengths ? 4 : 2;
         if (remaining > (long)(transmitBuffer.capacity() - lengthFieldSize) || !longLengths && remaining > 65535L) {
            throw Messages.msg.txMsgTooLarge();
         } else if ((long)transmitBuffer.remaining() < (long)lengthFieldSize + remaining && !this.writeBuffer()) {
            return false;
         } else {
            if (longLengths) {
               transmitBuffer.putInt((int)remaining);
            } else {
               transmitBuffer.putShort((short)((int)remaining));
            }

            Buffers.copy(transmitBuffer, srcs, offs, len);
            this.writeBuffer();
            return true;
         }
      }
   }

   public boolean sendFinal(ByteBuffer src) throws IOException {
      return Conduits.sendFinalBasic(this, src);
   }

   public boolean sendFinal(ByteBuffer[] srcs, int offs, int len) throws IOException {
      return Conduits.sendFinalBasic(this, srcs, offs, len);
   }

   private boolean writeBuffer() throws IOException {
      ByteBuffer buffer = (ByteBuffer)this.transmitBuffer.getResource();
      if (buffer.position() > 0) {
         buffer.flip();
      }

      boolean var7;
      try {
         while(buffer.hasRemaining()) {
            int res = ((StreamSinkConduit)this.next).write(buffer);
            if (res == 0) {
               boolean var3 = false;
               return var3;
            }
         }

         var7 = true;
      } finally {
         buffer.compact();
      }

      return var7;
   }

   public boolean flush() throws IOException {
      return this.writeBuffer() && ((StreamSinkConduit)this.next).flush();
   }

   public void terminateWrites() throws IOException {
      this.transmitBuffer.free();
      ((StreamSinkConduit)this.next).terminateWrites();
   }

   public void truncateWrites() throws IOException {
      this.transmitBuffer.free();
      ((StreamSinkConduit)this.next).truncateWrites();
   }
}
