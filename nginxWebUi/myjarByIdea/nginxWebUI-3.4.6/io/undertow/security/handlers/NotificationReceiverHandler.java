package io.undertow.security.handlers;

import io.undertow.security.api.NotificationReceiver;
import io.undertow.security.api.SecurityContext;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import java.util.Collection;

public class NotificationReceiverHandler implements HttpHandler {
   private final HttpHandler next;
   private final NotificationReceiver[] receivers;

   public NotificationReceiverHandler(HttpHandler next, Collection<NotificationReceiver> receivers) {
      this.next = next;
      this.receivers = (NotificationReceiver[])receivers.toArray(new NotificationReceiver[receivers.size()]);
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      SecurityContext sc = exchange.getSecurityContext();

      for(int i = 0; i < this.receivers.length; ++i) {
         sc.registerNotificationReceiver(this.receivers[i]);
      }

      this.next.handleRequest(exchange);
   }
}
