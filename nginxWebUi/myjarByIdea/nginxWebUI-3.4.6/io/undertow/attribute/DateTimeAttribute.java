package io.undertow.attribute;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.DateUtils;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateTimeAttribute implements ExchangeAttribute {
   public static final String DATE_TIME_SHORT = "%t";
   public static final String DATE_TIME = "%{DATE_TIME}";
   public static final String CUSTOM_TIME = "%{time,";
   public static final ExchangeAttribute INSTANCE = new DateTimeAttribute();
   private final String dateFormat;
   private final ThreadLocal<SimpleDateFormat> cachedFormat;

   private DateTimeAttribute() {
      this.dateFormat = null;
      this.cachedFormat = null;
   }

   public DateTimeAttribute(String dateFormat) {
      this(dateFormat, (String)null);
   }

   public DateTimeAttribute(final String dateFormat, final String timezone) {
      this.dateFormat = dateFormat;
      this.cachedFormat = new ThreadLocal<SimpleDateFormat>() {
         protected SimpleDateFormat initialValue() {
            SimpleDateFormat format = new SimpleDateFormat(dateFormat);
            if (timezone != null) {
               format.setTimeZone(TimeZone.getTimeZone(timezone));
            }

            return format;
         }
      };
   }

   public String readAttribute(HttpServerExchange exchange) {
      if (this.dateFormat == null) {
         return DateUtils.toCommonLogFormat(new Date());
      } else {
         SimpleDateFormat dateFormat = (SimpleDateFormat)this.cachedFormat.get();
         return dateFormat.format(new Date());
      }
   }

   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
      throw new ReadOnlyAttributeException("Date time", newValue);
   }

   public String toString() {
      return this.dateFormat == null ? "%{DATE_TIME}" : "%{time," + this.dateFormat + "}";
   }

   public static final class Builder implements ExchangeAttributeBuilder {
      public String name() {
         return "Date Time";
      }

      public ExchangeAttribute build(String token) {
         if (!token.equals("%{DATE_TIME}") && !token.equals("%t")) {
            return token.startsWith("%{time,") && token.endsWith("}") ? new DateTimeAttribute(token.substring("%{time,".length(), token.length() - 1)) : null;
         } else {
            return DateTimeAttribute.INSTANCE;
         }
      }

      public int priority() {
         return 0;
      }
   }
}
