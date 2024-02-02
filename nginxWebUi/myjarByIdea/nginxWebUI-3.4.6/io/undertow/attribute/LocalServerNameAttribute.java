package io.undertow.attribute;

import io.undertow.server.HttpServerExchange;

public class LocalServerNameAttribute implements ExchangeAttribute {
   public static final String LOCAL_SERVER_NAME_SHORT = "%v";
   public static final String LOCAL_SERVER_NAME = "%{LOCAL_SERVER_NAME}";
   public static final ExchangeAttribute INSTANCE = new LocalServerNameAttribute();

   private LocalServerNameAttribute() {
   }

   public String readAttribute(HttpServerExchange exchange) {
      return exchange.getHostName();
   }

   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
      throw new ReadOnlyAttributeException("Local server name", newValue);
   }

   public String toString() {
      return "%{LOCAL_SERVER_NAME}";
   }

   public static final class Builder implements ExchangeAttributeBuilder {
      public String name() {
         return "Local server name";
      }

      public ExchangeAttribute build(String token) {
         return !token.equals("%{LOCAL_SERVER_NAME}") && !token.equals("%v") ? null : LocalServerNameAttribute.INSTANCE;
      }

      public int priority() {
         return 0;
      }
   }
}
