package io.undertow.attribute;

import io.undertow.server.HttpServerExchange;

public class ThreadNameAttribute implements ExchangeAttribute {
   public static final String THREAD_NAME_SHORT = "%I";
   public static final String THREAD_NAME = "%{THREAD_NAME}";
   public static final ExchangeAttribute INSTANCE = new ThreadNameAttribute();

   private ThreadNameAttribute() {
   }

   public String readAttribute(HttpServerExchange exchange) {
      return Thread.currentThread().getName();
   }

   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
      throw new ReadOnlyAttributeException("Thread name", newValue);
   }

   public String toString() {
      return "%{THREAD_NAME}";
   }

   public static final class Builder implements ExchangeAttributeBuilder {
      public String name() {
         return "Thread name";
      }

      public ExchangeAttribute build(String token) {
         return !token.equals("%{THREAD_NAME}") && !token.equals("%I") ? null : ThreadNameAttribute.INSTANCE;
      }

      public int priority() {
         return 0;
      }
   }
}
