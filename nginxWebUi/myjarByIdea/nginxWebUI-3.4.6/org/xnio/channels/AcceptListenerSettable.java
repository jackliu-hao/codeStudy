package org.xnio.channels;

import java.nio.channels.Channel;
import org.xnio.ChannelListener;

public interface AcceptListenerSettable<C extends Channel> {
   ChannelListener<? super C> getAcceptListener();

   void setAcceptListener(ChannelListener<? super C> var1);

   public static class Setter<C extends Channel> implements ChannelListener.Setter<C> {
      private final AcceptListenerSettable<C> settable;

      public Setter(AcceptListenerSettable<C> settable) {
         this.settable = settable;
      }

      public void set(ChannelListener<? super C> listener) {
         this.settable.setAcceptListener(listener);
      }
   }
}
