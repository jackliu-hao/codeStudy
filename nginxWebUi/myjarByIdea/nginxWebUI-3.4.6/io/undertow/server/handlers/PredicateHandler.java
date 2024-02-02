package io.undertow.server.handlers;

import io.undertow.predicate.Predicate;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

public class PredicateHandler implements HttpHandler {
   private volatile Predicate predicate;
   private volatile HttpHandler trueHandler;
   private volatile HttpHandler falseHandler;

   public PredicateHandler(Predicate predicate, HttpHandler trueHandler, HttpHandler falseHandler) {
      this.predicate = predicate;
      this.trueHandler = trueHandler;
      this.falseHandler = falseHandler;
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      HttpHandler next = this.predicate.resolve(exchange) ? this.trueHandler : this.falseHandler;
      next.handleRequest(exchange);
   }

   public Predicate getPredicate() {
      return this.predicate;
   }

   public PredicateHandler setPredicate(Predicate predicate) {
      this.predicate = predicate;
      return this;
   }

   public HttpHandler getTrueHandler() {
      return this.trueHandler;
   }

   public PredicateHandler setTrueHandler(HttpHandler trueHandler) {
      this.trueHandler = trueHandler;
      return this;
   }

   public HttpHandler getFalseHandler() {
      return this.falseHandler;
   }

   public PredicateHandler setFalseHandler(HttpHandler falseHandler) {
      this.falseHandler = falseHandler;
      return this;
   }
}
