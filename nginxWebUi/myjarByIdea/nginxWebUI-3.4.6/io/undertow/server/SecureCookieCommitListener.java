package io.undertow.server;

import io.undertow.server.handlers.Cookie;
import java.util.Iterator;

public enum SecureCookieCommitListener implements ResponseCommitListener {
   INSTANCE;

   public void beforeCommit(HttpServerExchange exchange) {
      Iterator var2 = exchange.responseCookies().iterator();

      while(var2.hasNext()) {
         Cookie cookie = (Cookie)var2.next();
         cookie.setSecure(true);
      }

   }
}
