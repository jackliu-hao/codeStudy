package io.undertow.attribute;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.Cookie;
import io.undertow.server.handlers.CookieImpl;
import java.util.Iterator;

public class CookieAttribute implements ExchangeAttribute {
   private final String cookieName;

   public CookieAttribute(String cookieName) {
      this.cookieName = cookieName;
   }

   public String readAttribute(HttpServerExchange exchange) {
      Iterator var2 = exchange.requestCookies().iterator();

      Cookie cookie;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         cookie = (Cookie)var2.next();
      } while(!this.cookieName.equals(cookie.getName()));

      return cookie.getValue();
   }

   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
      exchange.setResponseCookie(new CookieImpl(this.cookieName, newValue));
   }

   public String toString() {
      return "%{c," + this.cookieName + "}";
   }

   public static final class Builder implements ExchangeAttributeBuilder {
      public String name() {
         return "Cookie";
      }

      public ExchangeAttribute build(String token) {
         if (token.startsWith("%{c,") && token.endsWith("}")) {
            String cookieName = token.substring(4, token.length() - 1);
            return new CookieAttribute(cookieName);
         } else {
            return null;
         }
      }

      public int priority() {
         return 0;
      }
   }
}
