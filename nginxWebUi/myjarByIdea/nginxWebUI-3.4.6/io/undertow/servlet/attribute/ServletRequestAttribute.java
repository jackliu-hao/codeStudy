package io.undertow.servlet.attribute;

import io.undertow.attribute.ExchangeAttribute;
import io.undertow.attribute.ExchangeAttributeBuilder;
import io.undertow.attribute.ReadOnlyAttributeException;
import io.undertow.server.HttpServerExchange;
import io.undertow.servlet.handlers.ServletRequestContext;
import java.util.HashMap;
import java.util.Map;

public class ServletRequestAttribute implements ExchangeAttribute {
   private final String attributeName;

   public ServletRequestAttribute(String attributeName) {
      this.attributeName = attributeName;
   }

   public String readAttribute(HttpServerExchange exchange) {
      ServletRequestContext context = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
      if (context != null) {
         Object result = context.getServletRequest().getAttribute(this.attributeName);
         if (result != null) {
            return result.toString();
         }
      } else {
         Map<String, String> attrs = (Map)exchange.getAttachment(HttpServerExchange.REQUEST_ATTRIBUTES);
         if (attrs != null) {
            return (String)attrs.get(this.attributeName);
         }
      }

      return null;
   }

   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
      ServletRequestContext context = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
      if (context != null) {
         context.getServletRequest().setAttribute(this.attributeName, newValue);
      } else {
         Map<String, String> attrs = (Map)exchange.getAttachment(HttpServerExchange.REQUEST_ATTRIBUTES);
         if (attrs == null) {
            exchange.putAttachment(HttpServerExchange.REQUEST_ATTRIBUTES, attrs = new HashMap());
         }

         ((Map)attrs).put(this.attributeName, newValue);
      }

   }

   public String toString() {
      return "%{r," + this.attributeName + "}";
   }

   public static final class Builder implements ExchangeAttributeBuilder {
      public String name() {
         return "Servlet request attribute";
      }

      public ExchangeAttribute build(String token) {
         if (token.startsWith("%{r,") && token.endsWith("}")) {
            String attributeName = token.substring(4, token.length() - 1);
            return new ServletRequestAttribute(attributeName);
         } else {
            return null;
         }
      }

      public int priority() {
         return 0;
      }
   }
}
