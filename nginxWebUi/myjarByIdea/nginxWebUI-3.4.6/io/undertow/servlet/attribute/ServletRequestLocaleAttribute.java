package io.undertow.servlet.attribute;

import io.undertow.attribute.ExchangeAttribute;
import io.undertow.attribute.ExchangeAttributeBuilder;
import io.undertow.attribute.ReadOnlyAttributeException;
import io.undertow.server.HttpServerExchange;
import io.undertow.servlet.handlers.ServletRequestContext;
import javax.servlet.ServletRequest;

public class ServletRequestLocaleAttribute implements ExchangeAttribute {
   public static final String REQUEST_LOCALE = "%{REQUEST_LOCALE}";
   public static final ServletRequestLocaleAttribute INSTANCE = new ServletRequestLocaleAttribute();

   public String readAttribute(HttpServerExchange exchange) {
      ServletRequestContext context = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
      if (context != null) {
         ServletRequest req = context.getServletRequest();
         return req.getLocale().toString();
      } else {
         return null;
      }
   }

   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
      throw new ReadOnlyAttributeException("Locale", newValue);
   }

   public String toString() {
      return "%{REQUEST_LOCALE}";
   }

   public static final class Builder implements ExchangeAttributeBuilder {
      public String name() {
         return "Request Locale";
      }

      public ExchangeAttribute build(String token) {
         return token.equals("%{REQUEST_LOCALE}") ? ServletRequestLocaleAttribute.INSTANCE : null;
      }

      public int priority() {
         return 0;
      }
   }
}
