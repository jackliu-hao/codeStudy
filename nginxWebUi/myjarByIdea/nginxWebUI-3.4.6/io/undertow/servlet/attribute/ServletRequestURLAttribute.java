package io.undertow.servlet.attribute;

import io.undertow.attribute.ExchangeAttribute;
import io.undertow.attribute.ExchangeAttributeBuilder;
import io.undertow.attribute.ReadOnlyAttributeException;
import io.undertow.attribute.RequestURLAttribute;
import io.undertow.server.HttpServerExchange;
import io.undertow.servlet.handlers.ServletRequestContext;

public class ServletRequestURLAttribute implements ExchangeAttribute {
   public static final String REQUEST_URL_SHORT = "%U";
   public static final String REQUEST_URL = "%{REQUEST_URL}";
   public static final ExchangeAttribute INSTANCE = new ServletRequestURLAttribute();

   private ServletRequestURLAttribute() {
   }

   public String readAttribute(HttpServerExchange exchange) {
      ServletRequestContext src = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
      if (src == null) {
         return RequestURLAttribute.INSTANCE.readAttribute(exchange);
      } else {
         String uri = (String)src.getServletRequest().getAttribute("javax.servlet.forward.request_uri");
         if (uri != null) {
            return uri;
         } else {
            uri = (String)src.getServletRequest().getAttribute("javax.servlet.error.request_uri");
            return uri != null ? uri : RequestURLAttribute.INSTANCE.readAttribute(exchange);
         }
      }
   }

   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
      RequestURLAttribute.INSTANCE.writeAttribute(exchange, newValue);
   }

   public String toString() {
      return "%{REQUEST_URL}";
   }

   public static final class Builder implements ExchangeAttributeBuilder {
      public String name() {
         return "Request URL";
      }

      public ExchangeAttribute build(String token) {
         return !token.equals("%{REQUEST_URL}") && !token.equals("%U") ? null : ServletRequestURLAttribute.INSTANCE;
      }

      public int priority() {
         return 1;
      }
   }
}
