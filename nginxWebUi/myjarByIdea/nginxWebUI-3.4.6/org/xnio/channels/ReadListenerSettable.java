package org.xnio.channels;

import java.nio.channels.Channel;
import org.xnio.ChannelListener;

public interface ReadListenerSettable<C extends Channel> {
   void setReadListener(ChannelListener<? super C> var1);

   ChannelListener<? super C> getReadListener();

   public static class Setter<C extends Channel> implements ChannelListener.Setter<C> {
      private final ReadListenerSettable<C> settable;

      public Setter(ReadListenerSettable<C> settable) {
         this.settable = settable;
      }

      public void set(ChannelListener<? super C> listener) {
         this.settable.setReadListener(listener);
      }
   }
}
