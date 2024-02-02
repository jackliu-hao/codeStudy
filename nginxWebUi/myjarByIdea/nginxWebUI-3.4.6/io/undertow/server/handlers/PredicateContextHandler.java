package io.undertow.server.handlers;

import io.undertow.predicate.Predicate;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import java.util.TreeMap;

public class PredicateContextHandler implements HttpHandler {
   private final HttpHandler next;

   public PredicateContextHandler(HttpHandler next) {
      this.next = next;
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      exchange.putAttachment(Predicate.PREDICATE_CONTEXT, new TreeMap());
      this.next.handleRequest(exchange);
   }
}
