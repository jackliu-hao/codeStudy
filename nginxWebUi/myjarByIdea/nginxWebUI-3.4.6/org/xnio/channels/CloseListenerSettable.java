package org.xnio.channels;

import java.nio.channels.Channel;
import org.xnio.ChannelListener;

public interface CloseListenerSettable<C extends Channel> {
   void setCloseListener(ChannelListener<? super C> var1);

   ChannelListener<? super C> getCloseListener();

   public static class Setter<C extends Channel> implements ChannelListener.Setter<C> {
      private final CloseListenerSettable<C> settable;

      public Setter(CloseListenerSettable<C> settable) {
         this.settable = settable;
      }

      public void set(ChannelListener<? super C> listener) {
         this.settable.setCloseListener(listener);
      }
   }
}
