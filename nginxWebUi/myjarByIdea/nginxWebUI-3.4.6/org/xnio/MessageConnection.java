package org.xnio;

import org.xnio._private.Messages;
import org.xnio.channels.CloseListenerSettable;
import org.xnio.conduits.ConduitReadableMessageChannel;
import org.xnio.conduits.ConduitWritableMessageChannel;
import org.xnio.conduits.MessageSinkConduit;
import org.xnio.conduits.MessageSourceConduit;

public abstract class MessageConnection extends Connection implements CloseListenerSettable<MessageConnection> {
   private ConduitReadableMessageChannel sourceChannel;
   private ConduitWritableMessageChannel sinkChannel;
   private ChannelListener<? super MessageConnection> closeListener;

   protected MessageConnection(XnioIoThread thread) {
      super(thread);
   }

   public void setCloseListener(ChannelListener<? super MessageConnection> listener) {
      this.closeListener = listener;
   }

   public ChannelListener<? super MessageConnection> getCloseListener() {
      return this.closeListener;
   }

   public ChannelListener.Setter<MessageConnection> getCloseSetter() {
      return new CloseListenerSettable.Setter(this);
   }

   protected void setSourceConduit(MessageSourceConduit conduit) {
      this.sourceChannel = conduit == null ? null : new ConduitReadableMessageChannel(this, conduit);
   }

   protected void setSinkConduit(MessageSinkConduit conduit) {
      this.sinkChannel = conduit == null ? null : new ConduitWritableMessageChannel(this, conduit);
   }

   void invokeCloseListener() {
      ChannelListeners.invokeChannelListener(this, this.closeListener);
   }

   private static <T> T notNull(T orig) throws IllegalStateException {
      if (orig == null) {
         throw Messages.msg.channelNotAvailable();
      } else {
         return orig;
      }
   }

   public ConduitReadableMessageChannel getSourceChannel() {
      return (ConduitReadableMessageChannel)notNull(this.sourceChannel);
   }

   public ConduitWritableMessageChannel getSinkChannel() {
      return (ConduitWritableMessageChannel)notNull(this.sinkChannel);
   }
}
