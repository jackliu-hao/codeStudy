package io.undertow.servlet.attribute;

import io.undertow.attribute.ExchangeAttribute;
import io.undertow.attribute.ExchangeAttributeBuilder;
import io.undertow.attribute.ReadOnlyAttributeException;
import io.undertow.server.HttpServerExchange;
import io.undertow.servlet.handlers.ServletRequestContext;

public class ServletRequestParameterAttribute implements ExchangeAttribute {
   private final String attributeName;

   public ServletRequestParameterAttribute(String attributeName) {
      this.attributeName = attributeName;
   }

   public String readAttribute(HttpServerExchange exchange) {
      ServletRequestContext context = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
      if (context != null) {
         Object result = context.getServletRequest().getParameter(this.attributeName);
         if (result != null) {
            return result.toString();
         }
      }

      return null;
   }

   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
      throw new ReadOnlyAttributeException();
   }

   public String toString() {
      return "%{rp," + this.attributeName + "}";
   }

   public static final class Builder implements ExchangeAttributeBuilder {
      public String name() {
         return "Servlet request parameter";
      }

      public ExchangeAttribute build(String token) {
         if (token.startsWith("%{rp,") && token.endsWith("}")) {
            String attributeName = token.substring(5, token.length() - 1);
            return new ServletRequestParameterAttribute(attributeName);
         } else {
            return null;
         }
      }

      public int priority() {
         return 0;
      }
   }
}
