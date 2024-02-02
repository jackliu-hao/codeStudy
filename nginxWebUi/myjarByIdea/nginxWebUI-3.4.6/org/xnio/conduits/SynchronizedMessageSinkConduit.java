package org.xnio.conduits;

import java.io.IOException;
import java.nio.ByteBuffer;

public final class SynchronizedMessageSinkConduit extends AbstractSynchronizedSinkConduit<MessageSinkConduit> implements MessageSinkConduit {
   public SynchronizedMessageSinkConduit(MessageSinkConduit next) {
      super(next);
   }

   public SynchronizedMessageSinkConduit(MessageSinkConduit next, Object lock) {
      super(next, lock);
   }

   public boolean send(ByteBuffer src) throws IOException {
      synchronized(this.lock) {
         return ((MessageSinkConduit)this.next).send(src);
      }
   }

   public boolean send(ByteBuffer[] srcs, int offs, int len) throws IOException {
      synchronized(this.lock) {
         return ((MessageSinkConduit)this.next).send(srcs, offs, len);
      }
   }

   public boolean sendFinal(ByteBuffer src) throws IOException {
      synchronized(this.lock) {
         return ((MessageSinkConduit)this.next).sendFinal(src);
      }
   }

   public boolean sendFinal(ByteBuffer[] srcs, int offs, int len) throws IOException {
      synchronized(this.lock) {
         return ((MessageSinkConduit)this.next).sendFinal(srcs, offs, len);
      }
   }
}
