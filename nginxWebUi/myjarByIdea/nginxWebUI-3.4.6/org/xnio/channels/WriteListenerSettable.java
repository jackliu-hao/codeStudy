package org.xnio.channels;

import java.nio.channels.Channel;
import org.xnio.ChannelListener;

public interface WriteListenerSettable<C extends Channel> {
   void setWriteListener(ChannelListener<? super C> var1);

   ChannelListener<? super C> getWriteListener();

   public static class Setter<C extends Channel> implements ChannelListener.Setter<C> {
      private final WriteListenerSettable<C> settable;

      public Setter(WriteListenerSettable<C> settable) {
         this.settable = settable;
      }

      public void set(ChannelListener<? super C> listener) {
         this.settable.setWriteListener(listener);
      }
   }
}
