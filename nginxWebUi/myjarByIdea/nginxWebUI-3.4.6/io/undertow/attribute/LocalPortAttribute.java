package io.undertow.attribute;

import io.undertow.server.HttpServerExchange;
import java.net.InetSocketAddress;

public class LocalPortAttribute implements ExchangeAttribute {
   public static final String LOCAL_PORT_SHORT = "%p";
   public static final String LOCAL_PORT = "%{LOCAL_PORT}";
   public static final ExchangeAttribute INSTANCE = new LocalPortAttribute();

   private LocalPortAttribute() {
   }

   public String readAttribute(HttpServerExchange exchange) {
      InetSocketAddress localAddress = (InetSocketAddress)exchange.getConnection().getLocalAddress();
      return Integer.toString(localAddress.getPort());
   }

   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
      throw new ReadOnlyAttributeException("Local port", newValue);
   }

   public String toString() {
      return "%{LOCAL_PORT}";
   }

   public static final class Builder implements ExchangeAttributeBuilder {
      public String name() {
         return "Local Port";
      }

      public ExchangeAttribute build(String token) {
         return !token.equals("%{LOCAL_PORT}") && !token.equals("%p") ? null : LocalPortAttribute.INSTANCE;
      }

      public int priority() {
         return 0;
      }
   }
}
