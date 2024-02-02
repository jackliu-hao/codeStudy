package io.undertow.servlet.attribute;

import io.undertow.attribute.ExchangeAttribute;
import io.undertow.attribute.ExchangeAttributeBuilder;
import io.undertow.attribute.ReadOnlyAttributeException;
import io.undertow.server.HttpServerExchange;
import io.undertow.servlet.handlers.ServletRequestContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class ServletSessionAttribute implements ExchangeAttribute {
   private final String attributeName;

   public ServletSessionAttribute(String attributeName) {
      this.attributeName = attributeName;
   }

   public String readAttribute(HttpServerExchange exchange) {
      ServletRequestContext context = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
      if (context != null) {
         ServletRequest req = context.getServletRequest();
         if (req instanceof HttpServletRequest) {
            HttpSession session = ((HttpServletRequest)req).getSession(false);
            if (session != null) {
               Object result = session.getAttribute(this.attributeName);
               if (result != null) {
                  return result.toString();
               }
            }
         }
      }

      return null;
   }

   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
      ServletRequestContext context = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
      if (context != null) {
         ServletRequest req = context.getServletRequest();
         if (req instanceof HttpServletRequest) {
            HttpSession session = ((HttpServletRequest)req).getSession(false);
            if (session != null) {
               session.setAttribute(this.attributeName, newValue);
            }
         }
      }

   }

   public String toString() {
      return "%{s," + this.attributeName + "}";
   }

   public static final class Builder implements ExchangeAttributeBuilder {
      public String name() {
         return "Servlet session attribute";
      }

      public ExchangeAttribute build(String token) {
         if (token.startsWith("%{s,") && token.endsWith("}")) {
            String attributeName = token.substring(4, token.length() - 1);
            return new ServletSessionAttribute(attributeName);
         } else {
            return null;
         }
      }

      public int priority() {
         return 0;
      }
   }
}
