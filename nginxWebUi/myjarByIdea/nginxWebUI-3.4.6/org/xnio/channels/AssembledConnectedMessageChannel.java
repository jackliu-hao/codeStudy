package org.xnio.channels;

import java.net.SocketAddress;
import org.xnio.ChannelListener;

public class AssembledConnectedMessageChannel extends AssembledMessageChannel implements ConnectedMessageChannel {
   private final ConnectedChannel connection;

   public AssembledConnectedMessageChannel(ConnectedChannel connection, ReadableMessageChannel readable, WritableMessageChannel writable) {
      super(connection, readable, writable);
      this.connection = connection;
   }

   public AssembledConnectedMessageChannel(ReadableMessageChannel readable, WritableMessageChannel writable) {
      this(new AssembledConnectedChannel(readable, writable), readable, writable);
   }

   public ChannelListener.Setter<? extends AssembledConnectedMessageChannel> getCloseSetter() {
      return super.getCloseSetter();
   }

   public ChannelListener.Setter<? extends AssembledConnectedMessageChannel> getReadSetter() {
      return super.getReadSetter();
   }

   public ChannelListener.Setter<? extends AssembledConnectedMessageChannel> getWriteSetter() {
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
