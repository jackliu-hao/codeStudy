package io.undertow.server.handlers;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.DateUtils;
import io.undertow.util.Headers;
import java.util.Date;

/** @deprecated */
@Deprecated
public class DateHandler implements HttpHandler {
   private final HttpHandler next;
   private volatile String cachedDateString;
   private volatile long nextUpdateTime = -1L;

   public DateHandler(HttpHandler next) {
      this.next = next;
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      long time = System.nanoTime();
      if (time < this.nextUpdateTime) {
         exchange.getResponseHeaders().put(Headers.DATE, this.cachedDateString);
      } else {
         long realTime = System.currentTimeMillis();
         String dateString = DateUtils.toDateString(new Date(realTime));
         this.cachedDateString = dateString;
         this.nextUpdateTime = time + 1000000000L;
         exchange.getResponseHeaders().put(Headers.DATE, dateString);
      }

      this.next.handleRequest(exchange);
   }
}
