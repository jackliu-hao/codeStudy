package io.undertow.attribute;

import io.undertow.server.HttpServerExchange;
import java.net.InetSocketAddress;

public class RemoteHostAttribute implements ExchangeAttribute {
   public static final String REMOTE_HOST_NAME_SHORT = "%h";
   public static final String REMOTE_HOST = "%{REMOTE_HOST}";
   public static final ExchangeAttribute INSTANCE = new RemoteHostAttribute();

   private RemoteHostAttribute() {
   }

   public String readAttribute(HttpServerExchange exchange) {
      InetSocketAddress sourceAddress = exchange.getSourceAddress();
      return sourceAddress.getHostString();
   }

   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
      throw new ReadOnlyAttributeException("Remote host", newValue);
   }

   public String toString() {
      return "%{REMOTE_HOST}";
   }

   public static final class Builder implements ExchangeAttributeBuilder {
      public String name() {
         return "Remote host";
      }

      public ExchangeAttribute build(String token) {
         return !token.equals("%{REMOTE_HOST}") && !token.equals("%h") ? null : RemoteHostAttribute.INSTANCE;
      }

      public int priority() {
         return 0;
      }
   }
}
