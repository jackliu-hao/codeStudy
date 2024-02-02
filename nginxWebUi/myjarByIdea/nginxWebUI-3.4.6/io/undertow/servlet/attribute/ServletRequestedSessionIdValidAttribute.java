package io.undertow.servlet.attribute;

import io.undertow.attribute.ExchangeAttribute;
import io.undertow.attribute.ExchangeAttributeBuilder;
import io.undertow.attribute.ReadOnlyAttributeException;
import io.undertow.server.HttpServerExchange;
import io.undertow.servlet.handlers.ServletRequestContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

public class ServletRequestedSessionIdValidAttribute implements ExchangeAttribute {
   public static final String REQUESTED_SESSION_ID_VALID = "%{REQUESTED_SESSION_ID_VALID}";
   public static final ServletRequestedSessionIdValidAttribute INSTANCE = new ServletRequestedSessionIdValidAttribute();

   public String readAttribute(HttpServerExchange exchange) {
      ServletRequestContext context = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
      if (context != null) {
         ServletRequest req = context.getServletRequest();
         if (req instanceof HttpServletRequest) {
            return Boolean.toString(((HttpServletRequest)req).isRequestedSessionIdValid());
         }
      }

      return null;
   }

   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
      throw new ReadOnlyAttributeException("Requested session ID from cookie", newValue);
   }

   public String toString() {
      return "%{REQUESTED_SESSION_ID_VALID}";
   }

   public static final class Builder implements ExchangeAttributeBuilder {
      public String name() {
         return "Requested Session ID from cookie attribute";
      }

      public ExchangeAttribute build(String token) {
         return token.equals("%{REQUESTED_SESSION_ID_VALID}") ? ServletRequestedSessionIdValidAttribute.INSTANCE : null;
      }

      public int priority() {
         return 0;
      }
   }
}
