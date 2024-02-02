package io.undertow.servlet.attribute;

import io.undertow.attribute.ExchangeAttribute;
import io.undertow.attribute.ExchangeAttributeBuilder;
import io.undertow.attribute.ReadOnlyAttributeException;
import io.undertow.server.HttpServerExchange;
import io.undertow.servlet.handlers.ServletRequestContext;

public class ServletNameAttribute implements ExchangeAttribute {
   public static final String SERVLET_NAME = "%{SERVLET_NAME}";
   public static final ExchangeAttribute INSTANCE = new ServletNameAttribute();
   public static final String NAME = "Servlet Name";

   private ServletNameAttribute() {
   }

   public String readAttribute(HttpServerExchange exchange) {
      ServletRequestContext src = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
      return src.getCurrentServlet().getManagedServlet().getServletInfo().getName();
   }

   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
      throw new ReadOnlyAttributeException("Servlet Name", newValue);
   }

   public String toString() {
      return "%{SERVLET_NAME}";
   }

   public static final class Builder implements ExchangeAttributeBuilder {
      public String name() {
         return "Servlet Name";
      }

      public ExchangeAttribute build(String token) {
         return token.equals("%{SERVLET_NAME}") ? ServletNameAttribute.INSTANCE : null;
      }

      public int priority() {
         return 0;
      }
   }
}
