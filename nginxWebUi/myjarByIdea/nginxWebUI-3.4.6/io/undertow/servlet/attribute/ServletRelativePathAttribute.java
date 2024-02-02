package io.undertow.servlet.attribute;

import io.undertow.attribute.ExchangeAttribute;
import io.undertow.attribute.ExchangeAttributeBuilder;
import io.undertow.attribute.ReadOnlyAttributeException;
import io.undertow.attribute.RelativePathAttribute;
import io.undertow.attribute.RequestURLAttribute;
import io.undertow.server.HttpServerExchange;
import io.undertow.servlet.handlers.ServletRequestContext;

public class ServletRelativePathAttribute implements ExchangeAttribute {
   public static final String RELATIVE_PATH_SHORT = "%R";
   public static final String RELATIVE_PATH = "%{RELATIVE_PATH}";
   public static final ExchangeAttribute INSTANCE = new ServletRelativePathAttribute();

   private ServletRelativePathAttribute() {
   }

   public String readAttribute(HttpServerExchange exchange) {
      ServletRequestContext src = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
      if (src == null) {
         return RequestURLAttribute.INSTANCE.readAttribute(exchange);
      } else {
         String path = (String)src.getServletRequest().getAttribute("javax.servlet.forward.path_info");
         String sp = (String)src.getServletRequest().getAttribute("javax.servlet.forward.servlet_path");
         if (path == null && sp == null) {
            return RequestURLAttribute.INSTANCE.readAttribute(exchange);
         } else if (sp == null) {
            return path;
         } else {
            return path == null ? sp : sp + path;
         }
      }
   }

   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
      RelativePathAttribute.INSTANCE.writeAttribute(exchange, newValue);
   }

   public String toString() {
      return "%{RELATIVE_PATH}";
   }

   public static final class Builder implements ExchangeAttributeBuilder {
      public String name() {
         return "Relative Path";
      }

      public ExchangeAttribute build(String token) {
         return !token.equals("%{RELATIVE_PATH}") && !token.equals("%R") ? null : ServletRelativePathAttribute.INSTANCE;
      }

      public int priority() {
         return 0;
      }
   }
}
