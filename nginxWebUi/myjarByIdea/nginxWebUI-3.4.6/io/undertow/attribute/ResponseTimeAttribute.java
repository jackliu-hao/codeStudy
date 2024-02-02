package io.undertow.attribute;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.AttachmentKey;
import java.util.concurrent.TimeUnit;

public class ResponseTimeAttribute implements ExchangeAttribute {
   private static final AttachmentKey<Long> FIRST_RESPONSE_TIME_NANOS = AttachmentKey.create(Long.class);
   public static final String RESPONSE_TIME_MILLIS_SHORT = "%D";
   public static final String RESPONSE_TIME_SECONDS_SHORT = "%T";
   public static final String RESPONSE_TIME_MILLIS = "%{RESPONSE_TIME}";
   public static final String RESPONSE_TIME_MICROS = "%{RESPONSE_TIME_MICROS}";
   public static final String RESPONSE_TIME_NANOS = "%{RESPONSE_TIME_NANOS}";
   private final TimeUnit timeUnit;

   public ResponseTimeAttribute(TimeUnit timeUnit) {
      this.timeUnit = timeUnit;
   }

   public String readAttribute(HttpServerExchange exchange) {
      long requestStartTime = exchange.getRequestStartTime();
      if (requestStartTime == -1L) {
         return null;
      } else {
         Long first = (Long)exchange.getAttachment(FIRST_RESPONSE_TIME_NANOS);
         long nanos;
         if (first != null) {
            nanos = first;
         } else {
            nanos = System.nanoTime() - requestStartTime;
            if (exchange.isResponseComplete()) {
               exchange.putAttachment(FIRST_RESPONSE_TIME_NANOS, nanos);
            }
         }

         if (this.timeUnit == TimeUnit.SECONDS) {
            StringBuilder buf = new StringBuilder();
            long milis = TimeUnit.MILLISECONDS.convert(nanos, TimeUnit.NANOSECONDS);
            buf.append(Long.toString(milis / 1000L));
            buf.append('.');
            int remains = (int)(milis % 1000L);
            buf.append(Long.toString((long)(remains / 100)));
            remains %= 100;
            buf.append(Long.toString((long)(remains / 10)));
            buf.append(Long.toString((long)(remains % 10)));
            return buf.toString();
         } else {
            return String.valueOf(this.timeUnit.convert(nanos, TimeUnit.NANOSECONDS));
         }
      }
   }

   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
      throw new ReadOnlyAttributeException("Response Time", newValue);
   }

   public String toString() {
      if (this.timeUnit.equals(TimeUnit.MILLISECONDS)) {
         return "%{RESPONSE_TIME}";
      } else if (this.timeUnit.equals(TimeUnit.SECONDS)) {
         return "%T";
      } else if (this.timeUnit.equals(TimeUnit.MICROSECONDS)) {
         return "%{RESPONSE_TIME_MICROS}";
      } else {
         return this.timeUnit.equals(TimeUnit.NANOSECONDS) ? "%{RESPONSE_TIME_NANOS}" : "ResponseTimeAttribute";
      }
   }

   public static final class Builder implements ExchangeAttributeBuilder {
      public String name() {
         return "Response Time";
      }

      public ExchangeAttribute build(String token) {
         if (!token.equals("%{RESPONSE_TIME}") && !token.equals("%D")) {
            if (token.equals("%T")) {
               return new ResponseTimeAttribute(TimeUnit.SECONDS);
            } else if (token.equals("%{RESPONSE_TIME_MICROS}")) {
               return new ResponseTimeAttribute(TimeUnit.MICROSECONDS);
            } else {
               return token.equals("%{RESPONSE_TIME_NANOS}") ? new ResponseTimeAttribute(TimeUnit.NANOSECONDS) : null;
            }
         } else {
            return new ResponseTimeAttribute(TimeUnit.MILLISECONDS);
         }
      }

      public int priority() {
         return 0;
      }
   }
}
