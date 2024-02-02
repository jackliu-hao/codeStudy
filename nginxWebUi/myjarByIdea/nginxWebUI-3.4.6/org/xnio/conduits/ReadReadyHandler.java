package org.xnio.conduits;

import java.io.Closeable;
import org.xnio.ChannelListener;
import org.xnio.ChannelListeners;
import org.xnio.IoUtils;
import org.xnio.channels.CloseListenerSettable;
import org.xnio.channels.ReadListenerSettable;
import org.xnio.channels.SuspendableReadChannel;

public interface ReadReadyHandler extends TerminateHandler {
   void readReady();

   public static class ReadyTask implements Runnable {
      private final ReadReadyHandler handler;

      public ReadyTask(ReadReadyHandler handler) {
         this.handler = handler;
      }

      public void run() {
         this.handler.readReady();
      }
   }

   public static class ChannelListenerHandler<C extends SuspendableReadChannel & ReadListenerSettable<C> & CloseListenerSettable<C>> implements ReadReadyHandler {
      private final C channel;

      public ChannelListenerHandler(C channel) {
         this.channel = channel;
      }

      public void forceTermination() {
         IoUtils.safeClose((Closeable)this.channel);
      }

      public void readReady() {
         ChannelListener<? super C> readListener = ((ReadListenerSettable)this.channel).getReadListener();
         if (readListener == null) {
            this.channel.suspendReads();
         } else {
            ChannelListeners.invokeChannelListener(this.channel, readListener);
         }

      }

      public void terminated() {
         ChannelListeners.invokeChannelListener(this.channel, ((CloseListenerSettable)this.channel).getCloseListener());
      }
   }
}
