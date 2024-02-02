package io.undertow.attribute;

import io.undertow.server.HttpServerExchange;

public class BytesSentAttribute implements ExchangeAttribute {
   public static final String BYTES_SENT_SHORT_UPPER = "%B";
   public static final String BYTES_SENT_SHORT_LOWER = "%b";
   public static final String BYTES_SENT = "%{BYTES_SENT}";
   private final boolean dashIfZero;

   public BytesSentAttribute(boolean dashIfZero) {
      this.dashIfZero = dashIfZero;
   }

   public String readAttribute(HttpServerExchange exchange) {
      if (this.dashIfZero) {
         long bytesSent = exchange.getResponseBytesSent();
         return bytesSent == 0L ? "-" : Long.toString(bytesSent);
      } else {
         return Long.toString(exchange.getResponseBytesSent());
      }
   }

   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
      throw new ReadOnlyAttributeException("Bytes sent", newValue);
   }

   public String toString() {
      return "%{BYTES_SENT}";
   }

   public static final class Builder implements ExchangeAttributeBuilder {
      public String name() {
         return "Bytes Sent";
      }

      public ExchangeAttribute build(String token) {
         if (token.equals("%b")) {
            return new BytesSentAttribute(true);
         } else {
            return !token.equals("%{BYTES_SENT}") && !token.equals("%B") ? null : new BytesSentAttribute(false);
         }
      }

      public int priority() {
         return 0;
      }
   }
}
