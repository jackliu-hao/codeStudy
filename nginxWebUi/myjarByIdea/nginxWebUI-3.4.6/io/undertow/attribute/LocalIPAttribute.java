package io.undertow.attribute;

import io.undertow.server.HttpServerExchange;
import java.net.InetSocketAddress;

public class LocalIPAttribute implements ExchangeAttribute {
   public static final String LOCAL_IP = "%{LOCAL_IP}";
   public static final String LOCAL_IP_SHORT = "%A";
   public static final ExchangeAttribute INSTANCE = new LocalIPAttribute();

   private LocalIPAttribute() {
   }

   public String readAttribute(HttpServerExchange exchange) {
      InetSocketAddress localAddress = (InetSocketAddress)exchange.getConnection().getLocalAddress();
      return localAddress.getAddress().getHostAddress();
   }

   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
      throw new ReadOnlyAttributeException("Local IP", newValue);
   }

   public String toString() {
      return "%{LOCAL_IP}";
   }

   public static final class Builder implements ExchangeAttributeBuilder {
      public String name() {
         return "Local IP";
      }

      public ExchangeAttribute build(String token) {
         return !token.equals("%{LOCAL_IP}") && !token.equals("%A") ? null : LocalIPAttribute.INSTANCE;
      }

      public int priority() {
         return 0;
      }
   }
}
