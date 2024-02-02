package io.undertow.attribute;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.Cookie;
import io.undertow.server.handlers.CookieImpl;
import java.util.Iterator;

public class RequestCookieAttribute implements ExchangeAttribute {
   private static final String TOKEN_PREFIX = "%{req-cookie,";
   private final String cookieName;

   public RequestCookieAttribute(String cookieName) {
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
      exchange.setRequestCookie(new CookieImpl(this.cookieName, newValue));
   }

   public String toString() {
      return "%{req-cookie," + this.cookieName + "}";
   }

   public static final class Builder implements ExchangeAttributeBuilder {
      public String name() {
         return "Request cookie";
      }

      public ExchangeAttribute build(String token) {
         if (token.startsWith("%{req-cookie,") && token.endsWith("}")) {
            String cookieName = token.substring("%{req-cookie,".length(), token.length() - 1);
            return new RequestCookieAttribute(cookieName);
         } else {
            return null;
         }
      }

      public int priority() {
         return 0;
      }
   }
}
