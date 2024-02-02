package org.xnio.conduits;

import java.io.Closeable;
import org.xnio.ChannelListener;
import org.xnio.ChannelListeners;
import org.xnio.IoUtils;
import org.xnio.channels.CloseListenerSettable;
import org.xnio.channels.SuspendableWriteChannel;
import org.xnio.channels.WriteListenerSettable;

public interface WriteReadyHandler extends TerminateHandler {
   void writeReady();

   public static class ReadyTask implements Runnable {
      private final WriteReadyHandler handler;

      public ReadyTask(WriteReadyHandler handler) {
         this.handler = handler;
      }

      public void run() {
         this.handler.writeReady();
      }
   }

   public static class ChannelListenerHandler<C extends SuspendableWriteChannel & WriteListenerSettable<C> & CloseListenerSettable<C>> implements WriteReadyHandler {
      private final C channel;

      public ChannelListenerHandler(C channel) {
         this.channel = channel;
      }

      public void forceTermination() {
         IoUtils.safeClose((Closeable)this.channel);
      }

      public void writeReady() {
         ChannelListener<? super C> writeListener = ((WriteListenerSettable)this.channel).getWriteListener();
         if (writeListener == null) {
            this.channel.suspendWrites();
         } else {
            ChannelListeners.invokeChannelListener(this.channel, writeListener);
         }

      }

      public void terminated() {
         ChannelListeners.invokeChannelListener(this.channel, ((CloseListenerSettable)this.channel).getCloseListener());
      }
   }
}
