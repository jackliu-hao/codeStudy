package org.xnio.channels;

import java.net.SocketAddress;

public final class SocketAddressBuffer {
   private SocketAddress sourceAddress;
   private SocketAddress destinationAddress;

   public SocketAddress getSourceAddress() {
      return this.sourceAddress;
   }

   public <A extends SocketAddress> A getSourceAddress(Class<A> type) {
      return type.isInstance(this.sourceAddress) ? (SocketAddress)type.cast(this.sourceAddress) : null;
   }

   public void setSourceAddress(SocketAddress sourceAddress) {
      this.sourceAddress = sourceAddress;
   }

   public SocketAddress getDestinationAddress() {
      return this.destinationAddress;
   }

   public <A extends SocketAddress> A getDestinationAddress(Class<A> type) {
      return type.isInstance(this.destinationAddress) ? (SocketAddress)type.cast(this.destinationAddress) : null;
   }

   public void setDestinationAddress(SocketAddress destinationAddress) {
      this.destinationAddress = destinationAddress;
   }

   public void clear() {
      this.sourceAddress = null;
      this.destinationAddress = null;
   }
}
