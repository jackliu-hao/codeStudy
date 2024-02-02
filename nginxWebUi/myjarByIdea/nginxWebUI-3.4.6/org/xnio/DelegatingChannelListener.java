package org.xnio;

import java.nio.channels.Channel;

public abstract class DelegatingChannelListener<T extends Channel> implements ChannelListener<T> {
   private final ChannelListener<? super T> next;

   protected DelegatingChannelListener(ChannelListener<? super T> next) {
      this.next = next;
   }

   protected void callNext(T channel) {
      ChannelListeners.invokeChannelListener(channel, this.next);
   }
}
