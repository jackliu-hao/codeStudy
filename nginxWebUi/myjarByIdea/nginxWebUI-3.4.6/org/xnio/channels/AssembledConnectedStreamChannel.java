package org.xnio.channels;

import java.net.SocketAddress;
import org.xnio.ChannelListener;

public class AssembledConnectedStreamChannel extends AssembledStreamChannel implements ConnectedStreamChannel {
   private final ConnectedChannel connection;

   public AssembledConnectedStreamChannel(ConnectedChannel connection, StreamSourceChannel source, StreamSinkChannel sink) {
      super(connection, source, sink);
      this.connection = connection;
   }

   public AssembledConnectedStreamChannel(StreamSourceChannel source, StreamSinkChannel sink) {
      this(new AssembledConnectedChannel(source, sink), source, sink);
   }

   public ChannelListener.Setter<? extends AssembledConnectedStreamChannel> getCloseSetter() {
      return super.getCloseSetter();
   }

   public ChannelListener.Setter<? extends AssembledConnectedStreamChannel> getReadSetter() {
      return super.getReadSetter();
   }

   public ChannelListener.Setter<? extends AssembledConnectedStreamChannel> getWriteSetter() {
      return super.getWriteSetter();
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
