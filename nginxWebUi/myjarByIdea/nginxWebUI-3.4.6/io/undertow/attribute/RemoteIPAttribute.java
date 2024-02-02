package io.undertow.attribute;

import io.undertow.server.HttpServerExchange;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class RemoteIPAttribute implements ExchangeAttribute {
   public static final String REMOTE_IP_SHORT = "%a";
   public static final String REMOTE_IP = "%{REMOTE_IP}";
   public static final ExchangeAttribute INSTANCE = new RemoteIPAttribute();

   private RemoteIPAttribute() {
   }

   public String readAttribute(HttpServerExchange exchange) {
      InetSocketAddress sourceAddress = exchange.getSourceAddress();
      InetAddress address = sourceAddress.getAddress();
      if (address == null) {
         address = ((InetSocketAddress)exchange.getConnection().getPeerAddress()).getAddress();
      }

      return address == null ? null : address.getHostAddress();
   }

   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
      throw new ReadOnlyAttributeException("Remote IP", newValue);
   }

   public String toString() {
      return "%{REMOTE_IP}";
   }

   public static final class Builder implements ExchangeAttributeBuilder {
      public String name() {
         return "Remote IP";
      }

      public ExchangeAttribute build(String token) {
         return !token.equals("%{REMOTE_IP}") && !token.equals("%a") ? null : RemoteIPAttribute.INSTANCE;
      }

      public int priority() {
         return 0;
      }
   }
}
