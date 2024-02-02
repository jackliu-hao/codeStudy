package io.undertow.servlet.attribute;

import io.undertow.attribute.ExchangeAttribute;
import io.undertow.attribute.ExchangeAttributeBuilder;
import io.undertow.attribute.ReadOnlyAttributeException;
import io.undertow.server.HttpServerExchange;
import io.undertow.servlet.handlers.ServletRequestContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

public class ServletRequestedSessionIdFromCookieAttribute implements ExchangeAttribute {
   public static final String REQUESTED_SESSION_ID_FROM_COOKIE = "%{REQUESTED_SESSION_ID_FROM_COOKIE}";
   public static final ServletRequestedSessionIdFromCookieAttribute INSTANCE = new ServletRequestedSessionIdFromCookieAttribute();

   public String readAttribute(HttpServerExchange exchange) {
      ServletRequestContext context = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
      if (context != null) {
         ServletRequest req = context.getServletRequest();
         if (req instanceof HttpServletRequest) {
            return Boolean.toString(((HttpServletRequest)req).isRequestedSessionIdFromCookie());
         }
      }

      return null;
   }

   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
      throw new ReadOnlyAttributeException("Requested session ID from cookie", newValue);
   }

   public String toString() {
      return "%{REQUESTED_SESSION_ID_FROM_COOKIE}";
   }

   public static final class Builder implements ExchangeAttributeBuilder {
      public String name() {
         return "Requested Session ID from cookie attribute";
      }

      public ExchangeAttribute build(String token) {
         return token.equals("%{REQUESTED_SESSION_ID_FROM_COOKIE}") ? ServletRequestedSessionIdFromCookieAttribute.INSTANCE : null;
      }

      public int priority() {
         return 0;
      }
   }
}
