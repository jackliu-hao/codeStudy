package io.undertow.servlet.attribute;

import io.undertow.attribute.ExchangeAttribute;
import io.undertow.attribute.ExchangeAttributeBuilder;
import io.undertow.attribute.ReadOnlyAttributeException;
import io.undertow.attribute.RequestLineAttribute;
import io.undertow.server.HttpServerExchange;
import io.undertow.servlet.handlers.ServletRequestContext;

public class ServletRequestLineAttribute implements ExchangeAttribute {
   public static final String REQUEST_LINE_SHORT = "%r";
   public static final String REQUEST_LINE = "%{REQUEST_LINE}";
   public static final ExchangeAttribute INSTANCE = new ServletRequestLineAttribute();

   private ServletRequestLineAttribute() {
   }

   public String readAttribute(HttpServerExchange exchange) {
      ServletRequestContext src = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
      if (src == null) {
         return RequestLineAttribute.INSTANCE.readAttribute(exchange);
      } else {
         StringBuilder sb = (new StringBuilder()).append(exchange.getRequestMethod().toString()).append(' ').append(ServletRequestURLAttribute.INSTANCE.readAttribute(exchange));
         String query = (String)src.getServletRequest().getAttribute("javax.servlet.forward.query_string");
         if (query != null && !query.isEmpty()) {
            sb.append('?');
            sb.append(query);
         } else if (!exchange.getQueryString().isEmpty()) {
            sb.append('?');
            sb.append(exchange.getQueryString());
         }

         sb.append(' ').append(exchange.getProtocol().toString()).toString();
         return sb.toString();
      }
   }

   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
      throw new ReadOnlyAttributeException("Request line", newValue);
   }

   public String toString() {
      return "%{REQUEST_LINE}";
   }

   public static final class Builder implements ExchangeAttributeBuilder {
      public String name() {
         return "Request line";
      }

      public ExchangeAttribute build(String token) {
         return !token.equals("%{REQUEST_LINE}") && !token.equals("%r") ? null : ServletRequestLineAttribute.INSTANCE;
      }

      public int priority() {
         return 1;
      }
   }
}
