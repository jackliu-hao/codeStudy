package org.xnio.sasl;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.xnio.Buffers;
import org.xnio.conduits.AbstractMessageSinkConduit;
import org.xnio.conduits.Conduits;
import org.xnio.conduits.MessageSinkConduit;

public final class SaslWrappingConduit extends AbstractMessageSinkConduit<MessageSinkConduit> implements MessageSinkConduit {
   private final SaslWrapper wrapper;
   private ByteBuffer buffer;

   public SaslWrappingConduit(MessageSinkConduit next, SaslWrapper wrapper) {
      super(next);
      this.wrapper = wrapper;
   }

   public boolean send(ByteBuffer src) throws IOException {
      if (!this.doSend()) {
         return false;
      } else {
         ByteBuffer wrapped = ByteBuffer.wrap(this.wrapper.wrap(src));
         if (!((MessageSinkConduit)this.next).send(wrapped)) {
            this.buffer = wrapped;
         }

         return true;
      }
   }

   public boolean send(ByteBuffer[] srcs, int offs, int len) throws IOException {
      if (!this.doSend()) {
         return false;
      } else {
         ByteBuffer wrapped = ByteBuffer.wrap(this.wrapper.wrap(Buffers.take(srcs, offs, len)));
         if (!((MessageSinkConduit)this.next).send(wrapped)) {
            this.buffer = wrapped;
         }

         return true;
      }
   }

   public boolean sendFinal(ByteBuffer src) throws IOException {
      return Conduits.sendFinalBasic(this, src);
   }

   public boolean sendFinal(ByteBuffer[] srcs, int offs, int len) throws IOException {
      return Conduits.sendFinalBasic(this, srcs, offs, len);
   }

   private boolean doSend() throws IOException {
      ByteBuffer buffer = this.buffer;
      if (buffer != null && ((MessageSinkConduit)this.next).send(buffer)) {
         this.buffer = null;
         return true;
      } else {
         return false;
      }
   }

   public boolean flush() throws IOException {
      return this.doSend() && ((MessageSinkConduit)this.next).flush();
   }
}
