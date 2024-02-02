package org.xnio;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import org.xnio._private.Messages;
import org.xnio.channels.CloseListenerSettable;
import org.xnio.conduits.ConduitStreamSinkChannel;
import org.xnio.conduits.ConduitStreamSourceChannel;
import org.xnio.conduits.StreamSinkConduit;
import org.xnio.conduits.StreamSourceConduit;

public abstract class StreamConnection extends Connection implements CloseListenerSettable<StreamConnection> {
   private static final ChannelListener<? super StreamConnection> INVOKED_CLOSE_LISTENER_FLAG = (connection) -> {
   };
   private ConduitStreamSourceChannel sourceChannel;
   private ConduitStreamSinkChannel sinkChannel;
   private AtomicReference<ChannelListener<? super StreamConnection>> closeListener = new AtomicReference();

   protected StreamConnection(XnioIoThread thread) {
      super(thread);
   }

   public void setCloseListener(ChannelListener<? super StreamConnection> listener) {
      ChannelListener currentListener;
      ChannelListener newListener;
      do {
         newListener = listener;
         currentListener = (ChannelListener)this.closeListener.get();
         if (currentListener != null) {
            if (currentListener == INVOKED_CLOSE_LISTENER_FLAG) {
               ChannelListeners.invokeChannelListener(this, listener);
               return;
            }

            newListener = this.mergeListeners(currentListener, listener);
         }
      } while(!this.closeListener.compareAndSet(currentListener, newListener));

   }

   private final ChannelListener<? super StreamConnection> mergeListeners(ChannelListener<? super StreamConnection> listener1, ChannelListener<? super StreamConnection> listener2) {
      return (channel) -> {
         listener1.handleEvent(channel);
         listener2.handleEvent(channel);
      };
   }

   protected void notifyReadClosed() {
      try {
         this.getSourceChannel().shutdownReads();
      } catch (IOException var2) {
         var2.printStackTrace();
      }

   }

   protected void notifyWriteClosed() {
      try {
         this.getSinkChannel().shutdownWrites();
      } catch (IOException var2) {
         var2.printStackTrace();
      }

   }

   public ChannelListener<? super StreamConnection> getCloseListener() {
      return (ChannelListener)this.closeListener.get();
   }

   public ChannelListener.Setter<? extends StreamConnection> getCloseSetter() {
      return new CloseListenerSettable.Setter(this);
   }

   protected void setSourceConduit(StreamSourceConduit conduit) {
      this.sourceChannel = conduit == null ? null : new ConduitStreamSourceChannel(this, conduit);
   }

   protected void setSinkConduit(StreamSinkConduit conduit) {
      this.sinkChannel = conduit == null ? null : new ConduitStreamSinkChannel(this, conduit);
   }

   void invokeCloseListener() {
      ChannelListener<? super StreamConnection> listener = (ChannelListener)this.closeListener.getAndSet(INVOKED_CLOSE_LISTENER_FLAG);
      ChannelListeners.invokeChannelListener(this, listener);
   }

   private static <T> T notNull(T orig) throws IllegalStateException {
      if (orig == null) {
         throw Messages.msg.channelNotAvailable();
      } else {
         return orig;
      }
   }

   public ConduitStreamSourceChannel getSourceChannel() {
      return (ConduitStreamSourceChannel)notNull(this.sourceChannel);
   }

   public ConduitStreamSinkChannel getSinkChannel() {
      return (ConduitStreamSinkChannel)notNull(this.sinkChannel);
   }
}
