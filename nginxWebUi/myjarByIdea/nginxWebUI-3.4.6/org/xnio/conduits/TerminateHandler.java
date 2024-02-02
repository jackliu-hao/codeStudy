package org.xnio.conduits;

import java.io.Closeable;
import java.nio.channels.Channel;
import org.xnio.ChannelListeners;
import org.xnio.IoUtils;
import org.xnio.channels.CloseListenerSettable;

public interface TerminateHandler {
   void forceTermination();

   void terminated();

   public static class ReadyTask implements Runnable {
      private final TerminateHandler handler;

      public ReadyTask(TerminateHandler handler) {
         this.handler = handler;
      }

      public void run() {
         this.handler.terminated();
      }
   }

   public static class ChannelListenerHandler<C extends Channel & CloseListenerSettable<C>> implements TerminateHandler {
      private final C channel;

      public ChannelListenerHandler(C channel) {
         this.channel = channel;
      }

      public void forceTermination() {
         IoUtils.safeClose((Closeable)this.channel);
      }

      public void terminated() {
         ChannelListeners.invokeChannelListener(this.channel, ((CloseListenerSettable)this.channel).getCloseListener());
      }
   }
}
