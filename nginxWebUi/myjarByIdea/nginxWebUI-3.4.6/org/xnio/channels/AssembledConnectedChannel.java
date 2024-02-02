package org.xnio.channels;

import java.net.SocketAddress;
import org.xnio.ChannelListener;
import org.xnio._private.Messages;

public class AssembledConnectedChannel extends AssembledChannel implements ConnectedChannel {
   private final ConnectedChannel connection;

   public AssembledConnectedChannel(SuspendableReadChannel readChannel, SuspendableWriteChannel writeChannel) {
      super(readChannel, writeChannel);
      ConnectedChannel ch = (ConnectedChannel)Channels.unwrap(ConnectedChannel.class, readChannel);
      if (ch == null) {
         ch = (ConnectedChannel)Channels.unwrap(ConnectedChannel.class, writeChannel);
      }

      if (ch == null) {
         throw Messages.msg.oneChannelMustBeConnection();
      } else {
         this.connection = ch;
      }
   }

   public ChannelListener.Setter<? extends AssembledConnectedChannel> getCloseSetter() {
      return super.getCloseSetter();
   }

   public SocketAddress getPeerAddress() {
      return this.connection.getPeerAddress();
   }

   public <A extends SocketAddress> A getPeerAddress(Class<A> type) {
      return this.connection.getPeerAddress(type);
   }

   public SocketAddress getLocalAddress() {
      return this.connection.getLocalAddress();
   }

   public <A extends SocketAddress> A getLocalAddress(Class<A> type) {
      return this.connection.getLocalAddress(type);
   }
}
