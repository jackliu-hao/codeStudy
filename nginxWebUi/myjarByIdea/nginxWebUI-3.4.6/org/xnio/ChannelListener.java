package org.xnio;

import java.nio.channels.Channel;
import java.util.EventListener;
import org.jboss.logging.Logger;
import org.xnio._private.Messages;

public interface ChannelListener<T extends Channel> extends EventListener {
   void handleEvent(T var1);

   public static class SimpleSetter<T extends Channel> implements Setter<T> {
      private ChannelListener<? super T> channelListener;

      public void set(ChannelListener<? super T> listener) {
         Messages.listenerMsg.logf(SimpleSetter.class.getName(), Logger.Level.TRACE, (Throwable)null, "Setting channel listener to %s", listener);
         this.channelListener = listener;
      }

      public ChannelListener<? super T> get() {
         return this.channelListener;
      }

      public String toString() {
         return "Simple channel listener setter (currently=" + this.channelListener + ")";
      }
   }

   public interface Setter<T extends Channel> {
      void set(ChannelListener<? super T> var1);
   }
}
