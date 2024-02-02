package io.undertow.attribute;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.Cookie;
import io.undertow.server.handlers.CookieImpl;
import java.util.Iterator;

public class ResponseCookieAttribute implements ExchangeAttribute {
   private static final String TOKEN_PREFIX = "%{resp-cookie,";
   private final String cookieName;

   public ResponseCookieAttribute(String cookieName) {
      this.cookieName = cookieName;
   }

   public String readAttribute(HttpServerExchange exchange) {
      Iterator var2 = exchange.responseCookies().iterator();

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
      return "%{resp-cookie," + this.cookieName + "}";
   }

   public static final class Builder implements ExchangeAttributeBuilder {
      public String name() {
         return "Response cookie";
      }

      public ExchangeAttribute build(String token) {
         if (token.startsWith("%{resp-cookie,") && token.endsWith("}")) {
            String cookieName = token.substring("%{resp-cookie,".length(), token.length() - 1);
            return new ResponseCookieAttribute(cookieName);
         } else {
            return null;
         }
      }

      public int priority() {
         return 0;
      }
   }
}
