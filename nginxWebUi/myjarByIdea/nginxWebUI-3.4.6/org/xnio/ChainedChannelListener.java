package org.xnio;

import java.nio.channels.Channel;

public final class ChainedChannelListener<T extends Channel> implements ChannelListener<T> {
   private final ChannelListener<? super T>[] listeners;

   public ChainedChannelListener(ChannelListener<? super T>... listeners) {
      this.listeners = (ChannelListener[])listeners.clone();
   }

   public void handleEvent(T channel) {
      ChannelListener[] var2 = this.listeners;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         ChannelListener<? super T> listener = var2[var4];
         ChannelListeners.invokeChannelListener(channel, listener);
      }

   }
}
