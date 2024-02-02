package io.undertow.servlet.attribute;

import io.undertow.attribute.ExchangeAttribute;
import io.undertow.attribute.ExchangeAttributeBuilder;
import io.undertow.attribute.ReadOnlyAttributeException;
import io.undertow.server.HttpServerExchange;
import io.undertow.servlet.handlers.ServletRequestContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class ServletSessionIdAttribute implements ExchangeAttribute {
   public static final String SESSION_ID_SHORT = "%S";
   public static final String SESSION_ID = "%{SESSION_ID}";
   public static final ServletSessionIdAttribute INSTANCE = new ServletSessionIdAttribute();

   public String readAttribute(HttpServerExchange exchange) {
      ServletRequestContext context = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
      if (context != null) {
         ServletRequest req = context.getServletRequest();
         if (req instanceof HttpServletRequest) {
            HttpSession session = ((HttpServletRequest)req).getSession(false);
            if (session != null) {
               return session.getId();
            }
         }
      }

      return null;
   }

   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
      throw new ReadOnlyAttributeException("Session ID", newValue);
   }

   public String toString() {
      return "%{SESSION_ID}";
   }

   public static final class Builder implements ExchangeAttributeBuilder {
      public String name() {
         return "Session ID attribute";
      }

      public ExchangeAttribute build(String token) {
         return !token.equals("%{SESSION_ID}") && !token.equals("%S") ? null : ServletSessionIdAttribute.INSTANCE;
      }

      public int priority() {
         return 0;
      }
   }
}
