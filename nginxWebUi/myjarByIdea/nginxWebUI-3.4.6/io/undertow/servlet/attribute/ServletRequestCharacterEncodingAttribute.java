package io.undertow.servlet.attribute;

import io.undertow.attribute.ExchangeAttribute;
import io.undertow.attribute.ExchangeAttributeBuilder;
import io.undertow.attribute.ReadOnlyAttributeException;
import io.undertow.server.HttpServerExchange;
import io.undertow.servlet.handlers.ServletRequestContext;
import javax.servlet.ServletRequest;

public class ServletRequestCharacterEncodingAttribute implements ExchangeAttribute {
   public static final String REQUEST_CHARACTER_ENCODING = "%{REQUEST_CHARACTER_ENCODING}";
   public static final ServletRequestCharacterEncodingAttribute INSTANCE = new ServletRequestCharacterEncodingAttribute();

   public String readAttribute(HttpServerExchange exchange) {
      ServletRequestContext context = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
      if (context != null) {
         ServletRequest req = context.getServletRequest();
         return req.getCharacterEncoding();
      } else {
         return null;
      }
   }

   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
      throw new ReadOnlyAttributeException("Request Character Encoding", newValue);
   }

   public String toString() {
      return "%{REQUEST_CHARACTER_ENCODING}";
   }

   public static final class Builder implements ExchangeAttributeBuilder {
      public String name() {
         return "Request Character Encoding";
      }

      public ExchangeAttribute build(String token) {
         return token.equals("%{REQUEST_CHARACTER_ENCODING}") ? ServletRequestCharacterEncodingAttribute.INSTANCE : null;
      }

      public int priority() {
         return 0;
      }
   }
}
